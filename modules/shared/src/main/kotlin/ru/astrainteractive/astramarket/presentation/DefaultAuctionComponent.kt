package ru.astrainteractive.astramarket.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.domain.model.AuctionSort
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.presentation.di.AuctionComponentDependencies
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev
import java.util.UUID

@Suppress("LongParameterList")
internal class DefaultAuctionComponent(
    private val playerUUID: UUID,
    private val isExpired: Boolean = false,
    private val dependencies: AuctionComponentDependencies
) : AuctionComponent,
    AsyncComponent(),
    AuctionComponentDependencies by dependencies {
    private val mainDispatcher = dispatchers.IO.limitedParallelism(1)

    override val model = MutableStateFlow(AuctionComponent.Model())

    override fun onSortButtonClicked(isRightClick: Boolean) {
        val newSortType = if (isRightClick) {
            model.value.sortType.next(AuctionSort.values())
        } else {
            model.value.sortType.prev(AuctionSort.values())
        }
        model.update {
            it.copy(sortType = newSortType)
        }
        sort()
    }

    private fun sort() {
        componentScope.launch(mainDispatcher) {
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
        AuctionComponent.ClickType.LEFT -> auctionBuyUseCase.invoke(AuctionBuyUseCase.Params(auction, playerUUID))
        AuctionComponent.ClickType.RIGHT -> removeAuctionUseCase.invoke(
            RemoveAuctionUseCase.Params(auction, playerUUID)
        )

        AuctionComponent.ClickType.MIDDLE -> expireAuctionUseCase.invoke(
            ExpireAuctionUseCase.Params(auction, playerUUID)
        )

        else -> false
    }

    override fun onAuctionItemClicked(i: Int, clickType: AuctionComponent.ClickType) {
        val auction = model.value.items.getOrNull(i) ?: return
        componentScope.launch(mainDispatcher) {
            val result = if (isExpired) {
                onExpiredAuctionClicked(auction)
            } else {
                onAuctionClicked(auction, clickType)
            }
            if (result) {
                loadItems()
                playerInteractionBridge.playSound(playerUUID) { config.sounds.sold }
            } else {
                playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            }
        }
    }

    override fun loadItems() {
        componentScope.launch(mainDispatcher) {
            val items = if (!isExpired) {
                marketApi.getSlots(isExpired)
            } else {
                marketApi.getUserSlots(playerUUID.toString(), isExpired)
            }
            model.update { model -> model.copy(items = items.orEmpty()) }
            sort()
        }
    }

    init {
        loadItems()
    }
}
