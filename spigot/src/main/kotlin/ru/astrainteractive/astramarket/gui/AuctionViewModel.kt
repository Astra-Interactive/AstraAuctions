package ru.astrainteractive.astramarket.gui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import ru.astrainteractive.astramarket.gui.domain.models.AuctionSort
import ru.astrainteractive.astramarket.gui.domain.usecases.AuctionBuyUseCase
import ru.astrainteractive.astramarket.gui.domain.usecases.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.usecases.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.util.sortBy
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev

class AuctionViewModel(
    private val player: Player,
    private val expired: Boolean = false,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val serializer: Serializer,
    private val auctionBuyUseCase: AuctionBuyUseCase,
    private val expireAuctionUseCase: ExpireAuctionUseCase,
    private val removeAuctionUseCase: RemoveAuctionUseCase
) : AsyncComponent() {

    private val _auctionList = MutableStateFlow(listOf<AuctionDTO>())
    val auctionList: StateFlow<List<AuctionDTO>>
        get() = _auctionList
    val maxItemsAmount: Int
        get() = _auctionList.value.size

    var sortType = AuctionSort.DATE_ASC
    fun onSortButtonClicked(isRightClick: Boolean) {
        sortType = if (isRightClick) {
            sortType.next(AuctionSort.values())
        } else {
            sortType.prev(AuctionSort.values())
        }
        sort()
    }

    private fun sort() {
        componentScope.launch(dispatchers.IO) {
            _auctionList.update {
                auctionList.value.sortBy(sortType, serializer)
            }
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: AuctionDTO): Boolean {
        return removeAuctionUseCase.invoke(RemoveAuctionUseCase.Params(auction, player))
    }

    private suspend fun onAuctionClicked(
        auction: AuctionDTO,
        clickType: ClickType
    ): Boolean {
        return when (clickType) {
            ClickType.LEFT -> auctionBuyUseCase.invoke(AuctionBuyUseCase.Params(auction, player))
            ClickType.RIGHT -> removeAuctionUseCase.invoke(RemoveAuctionUseCase.Params(auction, player))
            ClickType.MIDDLE -> expireAuctionUseCase.invoke(ExpireAuctionUseCase.Params(auction, player))
            else -> return false
        }
    }

    suspend fun onAuctionItemClicked(i: Int, clickType: ClickType): Boolean {
        val auction = _auctionList.value.getOrNull(i) ?: return false
        val result = if (expired) {
            onExpiredAuctionClicked(auction)
        } else {
            onAuctionClicked(auction, clickType)
        }
        if (result) {
            loadItems()
        }
        return result
    }

    fun loadItems() =
        componentScope.launch(dispatchers.IO) {
            val list =
                if (!expired) auctionsAPI.getAuctions(expired) else auctionsAPI.getUserAuctions(player.uuid, expired)
            val sorted = list?.sortBy(sortType, serializer) ?: emptyList()
            _auctionList.update {
                sorted
            }
        }

    init {
        loadItems()
    }
}
