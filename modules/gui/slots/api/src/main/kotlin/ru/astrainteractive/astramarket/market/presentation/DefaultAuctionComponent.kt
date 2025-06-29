package ru.astrainteractive.astramarket.market.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.CoroutineExt.launchWithLock
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.astramarket.market.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.market.presentation.di.AuctionComponentDependencies
import java.util.UUID

@Suppress("LongParameterList")
internal class DefaultAuctionComponent(
    private val playerUUID: UUID,
    private val targetPlayerUUID: UUID?,
    isExpired: Boolean,
    private val dependencies: AuctionComponentDependencies
) : AuctionComponent,
    CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO),
    AuctionComponentDependencies by dependencies {
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
        launchWithLock(mutex, dispatchers.IO) {
            model.update { model ->
                val input = SortAuctionsUseCase.Input(model.sortType, model.items)
                val sortedItems = sortAuctionsUseCase.invoke(input)
                model.copy(items = sortedItems.list)
            }
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: MarketSlot): Boolean {
        val param = RemoveAuctionUseCase.Params(auction, playerUUID)
        return removeAuctionUseCase.invoke(param)
    }

    private suspend fun onAuctionClicked(
        auction: MarketSlot,
        clickType: AuctionComponent.ClickType
    ) = when (clickType) {
        AuctionComponent.ClickType.LEFT -> {
            val param = AuctionBuyUseCase.Params(auction, playerUUID)
            auctionBuyUseCase.invoke(param)
        }

        AuctionComponent.ClickType.RIGHT -> {
            val param = RemoveAuctionUseCase.Params(auction, playerUUID)
            removeAuctionUseCase.invoke(param)
        }

        AuctionComponent.ClickType.MIDDLE -> {
            val param = ExpireAuctionUseCase.Params(auction, playerUUID)
            expireAuctionUseCase.invoke(param)
        }
    }

    private fun loadItems() {
        launchWithLock(mutex, dispatchers.IO) {
            val items = when {
                targetPlayerUUID != null -> {
                    marketApi.getUserSlots(
                        uuid = targetPlayerUUID.toString(),
                        isExpired = model.value.isExpired
                    )
                }

                else -> marketApi.getSlots(isExpired = model.value.isExpired)
            }
            model.update { model -> model.copy(items = items.orEmpty()) }
            sort()
        }
    }

    override fun onAuctionItemClicked(i: Int, clickType: AuctionComponent.ClickType) {
        val auction = model.value.items.getOrNull(i) ?: return
        launchWithLock(mutex, dispatchers.IO) {
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
