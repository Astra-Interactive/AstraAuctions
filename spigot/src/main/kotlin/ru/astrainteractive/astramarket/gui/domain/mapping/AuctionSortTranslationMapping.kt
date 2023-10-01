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
        AuctionSort.MATERIAL_DESC -> translation.sortMaterialDesc
        AuctionSort.MATERIAL_ASC -> translation.sortMaterialAsc
        AuctionSort.DATE_DESC -> translation.sortDateDesc
        AuctionSort.DATE_ASC -> translation.sortDateAsc
        AuctionSort.NAME_DESC -> translation.sortNameDesc
        AuctionSort.NAME_ASC -> translation.sortNameAsc
        AuctionSort.PRICE_DESC -> translation.sortPriceDesc
        AuctionSort.PRICE_ASC -> translation.sortPriceAsc
        AuctionSort.PLAYER_ASC -> translation.sortPlayerAsc
        AuctionSort.PLAYER_DESC -> translation.sortPlayerDesc
    }
}
