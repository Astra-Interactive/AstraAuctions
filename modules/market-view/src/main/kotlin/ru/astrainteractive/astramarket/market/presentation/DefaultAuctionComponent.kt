package ru.astrainteractive.astramarket.market.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.CoroutineExt.launchWithLock
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.astramarket.market.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.market.presentation.di.AuctionComponentDependencies
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev
import java.util.UUID

@Suppress("LongParameterList")
internal class DefaultAuctionComponent(
    private val playerUUID: UUID,
    private val targetPlayerUUID: UUID?,
    isExpired: Boolean,
    private val dependencies: AuctionComponentDependencies
) : AuctionComponent,
    AsyncComponent(),
    AuctionComponentDependencies by dependencies {
    private val mutex = Mutex()

    override val model = MutableStateFlow(
        AuctionComponent.Model(
            isExpired = isExpired,
            targetPlayerUUID = targetPlayerUUID
        )
    )

    override fun onSortButtonClicked(isRightClick: Boolean) {
        val newSortType = if (isRightClick) {
            model.value.sortType.next(AuctionSort.entries.toTypedArray())
        } else {
            model.value.sortType.prev(AuctionSort.entries.toTypedArray())
        }
        model.update { it.copy(sortType = newSortType) }
        sort()
    }

    private fun sort() {
        componentScope.launchWithLock(mutex, dispatchers.IO) {
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
        componentScope.launchWithLock(mutex, dispatchers.IO) {
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
        componentScope.launchWithLock(mutex, dispatchers.IO) {
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
