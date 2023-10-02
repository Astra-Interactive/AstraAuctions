package ru.astrainteractive.astramarket.gui.domain.mapping

import ru.astrainteractive.astramarket.gui.domain.model.AuctionSort
import ru.astrainteractive.astramarket.plugin.Translation

interface AuctionSortTranslationMapping {
    fun translate(auctionSort: AuctionSort): String
}

internal class AuctionSortTranslationMappingImpl(
    private val translation: Translation
) : AuctionSortTranslationMapping {
    override fun translate(auctionSort: AuctionSort): String = when (auctionSort) {
        AuctionSort.MATERIAL_DESC -> translation.auction.sortMaterialDesc
        AuctionSort.MATERIAL_ASC -> translation.auction.sortMaterialAsc
        AuctionSort.DATE_DESC -> translation.auction.sortDateDesc
        AuctionSort.DATE_ASC -> translation.auction.sortDateAsc
        AuctionSort.NAME_DESC -> translation.auction.sortNameDesc
        AuctionSort.NAME_ASC -> translation.auction.sortNameAsc
        AuctionSort.PRICE_DESC -> translation.auction.sortPriceDesc
        AuctionSort.PRICE_ASC -> translation.auction.sortPriceAsc
        AuctionSort.PLAYER_ASC -> translation.auction.sortPlayerAsc
        AuctionSort.PLAYER_DESC -> translation.auction.sortPlayerDesc
    }
}
