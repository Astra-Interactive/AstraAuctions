package ru.astrainteractive.astramarket.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.model.AuctionSort

interface AuctionComponent {
    val model: StateFlow<Model>

    data class Model(
        val items: List<AuctionDTO> = emptyList(),
        val sortType: AuctionSort = AuctionSort.DATE_ASC
    ) {
        val maxItemsAmount = items.size
    }

    enum class ClickType {
        LEFT, RIGHT, MIDDLE
    }

    fun loadItems()
    fun onAuctionItemClicked(i: Int, clickType: ClickType)
    fun onSortButtonClicked(isRightClick: Boolean)
    fun close()
}
