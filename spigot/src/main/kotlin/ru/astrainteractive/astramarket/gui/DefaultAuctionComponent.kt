package ru.astrainteractive.astramarket.gui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.util.uuid
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.model.AuctionSort
import ru.astrainteractive.astramarket.gui.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.sortBy
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.util.playSound
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev

class DefaultAuctionComponent(
    private val player: Player,
    private val expired: Boolean = false,
    private val config: AuctionConfig,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val serializer: Serializer,
    private val auctionBuyUseCase: AuctionBuyUseCase,
    private val expireAuctionUseCase: ExpireAuctionUseCase,
    private val removeAuctionUseCase: RemoveAuctionUseCase
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
                model.copy(
                    items = model.items.sortBy(model.sortType, serializer)
                )
            }
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: AuctionDTO): Boolean {
        val param = RemoveAuctionUseCase.Params(auction, player)
        return removeAuctionUseCase.invoke(param)
    }

    private suspend fun onAuctionClicked(
        auction: AuctionDTO,
        clickType: ClickType
    ) = when (clickType) {
        ClickType.LEFT -> auctionBuyUseCase.invoke(AuctionBuyUseCase.Params(auction, player))
        ClickType.RIGHT -> removeAuctionUseCase.invoke(RemoveAuctionUseCase.Params(auction, player))
        ClickType.MIDDLE -> expireAuctionUseCase.invoke(ExpireAuctionUseCase.Params(auction, player))
        else -> false
    }

    override fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        val auction = model.value.items.getOrNull(i) ?: return
        componentScope.launch(mainDispatcher) {
            val result = if (expired) {
                onExpiredAuctionClicked(auction)
            } else {
                onAuctionClicked(auction, clickType)
            }
            if (result) {
                loadItems()
                player.playSound(config.sounds.sold)
            } else {
                player.playSound(config.sounds.fail)
            }
        }
    }

    override fun loadItems() {
        componentScope.launch(mainDispatcher) {
            val items = if (!expired) {
                auctionsAPI.getAuctions(expired)
            } else {
                auctionsAPI.getUserAuctions(player.uuid, expired)
            }
            model.update { model -> model.copy(items = items.orEmpty()) }
            sort()
        }
    }

    init {
        loadItems()
    }
}
