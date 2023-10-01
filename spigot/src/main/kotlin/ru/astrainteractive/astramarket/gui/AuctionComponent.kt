package ru.astrainteractive.astramarket.gui

import kotlinx.coroutines.flow.StateFlow
import org.bukkit.event.inventory.ClickType
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.model.AuctionSort

interface AuctionComponent {
    val model: StateFlow<Model>

    data class Model(
        val items: List<AuctionDTO> = emptyList(),
        val sortType: AuctionSort = AuctionSort.DATE_ASC
    ) {
        val maxItemsAmount = items.size
    }

    fun loadItems()
    fun onAuctionItemClicked(i: Int, clickType: ClickType)
    fun onSortButtonClicked(isRightClick: Boolean)
    fun close()
}
