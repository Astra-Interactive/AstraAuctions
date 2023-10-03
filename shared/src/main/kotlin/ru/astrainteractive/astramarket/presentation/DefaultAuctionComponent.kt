package ru.astrainteractive.astramarket.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.domain.model.AuctionSort
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.domain.util.AuctionSorter
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev
import java.util.UUID

@Suppress("LongParameterList")
class DefaultAuctionComponent(
    private val playerUUID: UUID,
    private val expired: Boolean = false,
    private val config: AuctionConfig,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val auctionBuyUseCase: AuctionBuyUseCase,
    private val expireAuctionUseCase: ExpireAuctionUseCase,
    private val removeAuctionUseCase: RemoveAuctionUseCase,
    private val playerInteraction: PlayerInteraction,
    private val auctionSorter: AuctionSorter
) : AuctionComponent, AsyncComponent() {
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
                val sortedItems = auctionSorter.sort(model.sortType, model.items)
                model.copy(items = sortedItems)
            }
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: AuctionDTO): Boolean {
        val param = RemoveAuctionUseCase.Params(auction, playerUUID)
        return removeAuctionUseCase.invoke(param)
    }

    private suspend fun onAuctionClicked(
        auction: AuctionDTO,
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
            val result = if (expired) {
                onExpiredAuctionClicked(auction)
            } else {
                onAuctionClicked(auction, clickType)
            }
            if (result) {
                loadItems()
                playerInteraction.playSound(playerUUID, config.sounds.sold)
            } else {
                playerInteraction.playSound(playerUUID, config.sounds.fail)
            }
        }
    }

    override fun loadItems() {
        componentScope.launch(mainDispatcher) {
            val items = if (!expired) {
                auctionsAPI.getAuctions(expired)
            } else {
                auctionsAPI.getUserAuctions(playerUUID.toString(), expired)
            }
            model.update { model -> model.copy(items = items.orEmpty()) }
            sort()
        }
    }

    init {
        loadItems()
    }
}
