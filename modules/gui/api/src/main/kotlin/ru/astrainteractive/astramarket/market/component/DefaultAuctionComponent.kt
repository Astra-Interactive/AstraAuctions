package ru.astrainteractive.astramarket.market.component

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.api.AuctionsSorter
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.model.AuctionSort
import ru.astrainteractive.astramarket.market.usecase.BuyAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.util.nextAfter
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
    private val auctionsSorter: AuctionsSorter,
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
        val newSortType = sorts.nextAfter(model.value.sortType, backwards = isRightClick)
        model.update { it.copy(sortType = newSortType) }
        sort()
    }

    private fun sort() {
        launch(mutex) {
            model.update { model ->
                model.copy(items = auctionsSorter.sort(model.items, model.sortType))
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
