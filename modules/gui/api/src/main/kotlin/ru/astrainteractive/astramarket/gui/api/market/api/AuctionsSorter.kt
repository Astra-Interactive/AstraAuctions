package ru.astrainteractive.astramarket.gui.api.market.api

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.api.market.model.AuctionSort

interface AuctionsSorter {
    fun sort(slots: List<MarketSlot>, sort: AuctionSort): List<MarketSlot>
}
