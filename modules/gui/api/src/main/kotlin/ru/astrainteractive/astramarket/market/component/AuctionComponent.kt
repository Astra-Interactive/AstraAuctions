package ru.astrainteractive.astramarket.market.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.model.AuctionSort
import java.util.UUID

interface AuctionComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun onAuctionItemClicked(i: Int, clickType: ClickType)
    fun onSortButtonClicked(isRightClick: Boolean)
    fun toggleExpired()

    data class Model(
        val items: List<MarketSlot>,
        val sortType: AuctionSort,
        val isExpired: Boolean,
        val targetPlayerUUID: UUID?
    )

    enum class ClickType {
        LEFT, RIGHT, MIDDLE
    }
}
