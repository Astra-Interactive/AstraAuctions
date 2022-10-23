package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.api.use_cases.AuctionBuyUseCase
import com.astrainteractive.astratemplate.api.use_cases.ExpireAuctionUseCase
import com.astrainteractive.astratemplate.api.use_cases.RemoveAuctionUseCase
import com.astrainteractive.astratemplate.api.entities.Auction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.next
import ru.astrainteractive.astralibs.utils.prev
import ru.astrainteractive.astralibs.utils.uuid

class ViewModel(private val player: Player, private val expired: Boolean = false) {
    private val _auctionList = MutableStateFlow(listOf<Auction>())
    val auctionList: StateFlow<List<Auction>>
        get() = _auctionList
    val maxItemsAmount: Int
        get() = _auctionList.value.size

    var sortType = AuctionSort.DATE_ASC
    private val auctionBuyUseCase = AuctionBuyUseCase()
    private val expireAuctionUseCase = ExpireAuctionUseCase()
    private val removeAuctionUseCase = RemoveAuctionUseCase()
    fun onSortButtonClicked(isRightClick: Boolean) {
        sortType = if (isRightClick)
            sortType.next()
        else
            sortType.prev()
        sort()
    }

    fun sort() {
        val sorted = Repository.sortBy(sortType, auctionList.value)
        PluginScope.launch {
            _auctionList.emit(sorted)
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: Auction): Boolean {
        return removeAuctionUseCase(RemoveAuctionUseCase.Params(auction, player))
    }

    private suspend fun onAuctionClicked(auction: Auction, clickType: ClickType): Boolean {
        return when (clickType) {
            ClickType.LEFT -> auctionBuyUseCase(AuctionBuyUseCase.Params(auction, player))
            ClickType.RIGHT -> removeAuctionUseCase(RemoveAuctionUseCase.Params(auction, player))
            ClickType.MIDDLE -> expireAuctionUseCase(ExpireAuctionUseCase.Params(auction, player))
            else -> return false
        }
    }

    suspend fun onAuctionItemClicked(i: Int, clickType: ClickType): Boolean {
        val auction = _auctionList.value.getOrNull(i) ?: return false
        val result = if (expired) onExpiredAuctionClicked(auction)
        else onAuctionClicked(auction, clickType)
        if (result)
            loadItems()
        return result
    }

    fun loadItems() =
        PluginScope.launch {
            val list = Repository.fetchAuctions(if (expired) player.uuid else null,expired)
            val sorted = Repository.sortBy(sortType, list)
            _auctionList.emit(sorted)
        }

    init {
        loadItems()
    }
}