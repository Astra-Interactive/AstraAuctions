package ru.astrainteractive.astramarket.market.api

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.model.AuctionSort

interface AuctionsSorter {
    fun sort(slots: List<MarketSlot>, sort: AuctionSort): List<MarketSlot>
}
