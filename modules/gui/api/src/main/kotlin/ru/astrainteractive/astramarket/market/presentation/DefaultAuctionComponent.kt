package ru.astrainteractive.astramarket.market.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.market.usecase.BuyAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.RemoveAuctionUseCase
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.coroutines.launch
import java.util.UUID

@Suppress("LongParameterList")
internal class DefaultAuctionComponent(
    private val playerUUID: UUID,
    private val targetPlayerUUID: UUID?,
    isExpired: Boolean,
    private val configKrate: CachedKrate<PluginConfig>,
    private val marketRepository: MarketRepository,
    private val sortAuctionsUseCase: SortAuctionsUseCase,
    private val removeAuctionUseCase: RemoveAuctionUseCase,
    private val buyAuctionUseCase: BuyAuctionUseCase,
    private val expireAuctionUseCase: ExpireAuctionUseCase,
    private val playerInteractionBridge: PlayerInteractionBridge
) : AuctionComponent,
    CoroutineFeature by CoroutineFeature.IO.withTimings() {
    private val config by configKrate

    private val mutex = Mutex()

    override val model = MutableStateFlow(
        AuctionComponent.Model(
            isExpired = isExpired,
            targetPlayerUUID = targetPlayerUUID
        )
    )

    override fun onSortButtonClicked(isRightClick: Boolean) {
        val sorts = listOf(
            AuctionSort.Date(false),
            AuctionSort.Date(true),
            AuctionSort.Material(false),
            AuctionSort.Material(true),
            AuctionSort.Name(false),
            AuctionSort.Name(true),
            AuctionSort.Price(false),
            AuctionSort.Price(true),
            AuctionSort.Player(false),
            AuctionSort.Player(true),
        )
        val i = sorts.indexOfFirst { sortType -> sortType == model.value.sortType }
        val offset = if (isRightClick) -1 else 1

        val newSortType = if (i == -1) {
            sorts.first()
        } else {
            sorts[(i + offset) % sorts.size]
        }
        model.update { it.copy(sortType = newSortType) }
        sort()
    }

    private fun sort() {
        launch(mutex) {
            model.update { model ->
                val input = SortAuctionsUseCase.Input(model.sortType, model.items)
                val sortedItems = sortAuctionsUseCase.invoke(input)
                model.copy(items = sortedItems.list)
            }
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: MarketSlot): Boolean {
        return removeAuctionUseCase.invoke(auction, playerUUID)
    }

    private suspend fun onAuctionClicked(
        auction: MarketSlot,
        clickType: AuctionComponent.ClickType
    ) = when (clickType) {
        AuctionComponent.ClickType.LEFT -> buyAuctionUseCase.invoke(auction, playerUUID)
        AuctionComponent.ClickType.RIGHT -> removeAuctionUseCase.invoke(auction, playerUUID)
        AuctionComponent.ClickType.MIDDLE -> expireAuctionUseCase.invoke(auction, playerUUID)
    }

    private fun loadItems() {
        launch(mutex) {
            val items = when {
                targetPlayerUUID != null -> {
                    marketRepository.getUserSlots(
                        uuid = targetPlayerUUID.toString(),
                        isExpired = model.value.isExpired
                    )
                }

                else -> marketRepository.getSlots(isExpired = model.value.isExpired)
            }
            model.update { model -> model.copy(items = items.getOrDefault(emptyList())) }
            sort()
        }
    }

    override fun onAuctionItemClicked(i: Int, clickType: AuctionComponent.ClickType) {
        val auction = model.value.items.getOrNull(i) ?: return
        launch(mutex) {
            val result = when (model.value.isExpired) {
                true -> {
                    if (auction.minecraftUuid == playerUUID.toString()) {
                        onExpiredAuctionClicked(auction)
                    } else {
                        onAuctionClicked(auction, clickType)
                    }
                }

                false -> onAuctionClicked(auction, clickType)
            }
            if (result) {
                loadItems()
                playerInteractionBridge.playSound(playerUUID) { config.sounds.sold }
            } else {
                playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            }
        }
    }

    override fun toggleExpired() {
        model.update { it.copy(isExpired = !it.isExpired) }
        loadItems()
    }

    init {
        loadItems()
    }
}
