package ru.astrainteractive.astramarket.domain.util

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.model.AuctionSort

interface AuctionSorter {
    fun sort(sortType: AuctionSort, list: List<AuctionDTO>): List<AuctionDTO>
}
