package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.api.SortType
import com.astrainteractive.astratemplate.api.next
import com.astrainteractive.astratemplate.api.prev
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

class ViewModel(private val player: Player, private val expired: Boolean = false) {
    private val _auctionList = MutableStateFlow(listOf<Auction>())
    val auctionList: StateFlow<List<Auction>>
        get() = _auctionList
    val maxItemsAmount: Int
        get() = _auctionList.value.size

    var sortType = SortType.DATE_ASC

    fun onSortButtonClicked(isRightClick: Boolean) {
        val type = if (isRightClick)
            sortType.next()
        else
            sortType.prev()
        sort()
    }

    fun sort() {
        val sorted = Repository.sortBy(sortType,auctionList.value)
        AsyncHelper.launch {
            _auctionList.emit(sorted)
        }
    }

    private suspend fun onExpiredAuctionClicked(auction: Auction): Boolean {
        return Repository.removeAuction(auction, player)
    }

    private suspend fun onAuctionClicked(auction: Auction, clickType: ClickType): Boolean {
        return when (clickType) {
            ClickType.LEFT -> Repository.buyAuction(auction, player)
            ClickType.RIGHT -> Repository.removeAuction(auction, player)
            ClickType.MIDDLE -> Repository.forceExpireAuction(player, auction)
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
        AsyncHelper.launch {
            val list = Repository.loadAuctions(if (expired) player.uuid else null)
            val sorted = Repository.sortBy(sortType, list)
            _auctionList.emit(sorted)
        }

    init {
        loadItems()
    }
}