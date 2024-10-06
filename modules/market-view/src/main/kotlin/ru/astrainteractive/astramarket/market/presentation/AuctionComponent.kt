package ru.astrainteractive.astramarket.market.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import java.util.UUID

interface AuctionComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun onAuctionItemClicked(i: Int, clickType: ClickType)
    fun onSortButtonClicked(isRightClick: Boolean)
    fun toggleExpired()

    data class Model(
        val items: List<MarketSlot> = emptyList(),
        val sortType: AuctionSort = AuctionSort.DATE_DESC,
        val isExpired: Boolean,
        val targetPlayerUUID: UUID?
    )

    enum class ClickType {
        LEFT, RIGHT, MIDDLE
    }
}
