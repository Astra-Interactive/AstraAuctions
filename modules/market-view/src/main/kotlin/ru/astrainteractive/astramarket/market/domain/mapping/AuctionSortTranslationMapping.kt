package ru.astrainteractive.astramarket.market.domain.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.klibs.kstorage.api.Krate

interface AuctionSortTranslationMapping {
    fun translate(auctionSort: AuctionSort): StringDesc.Raw
}

internal class AuctionSortTranslationMappingImpl(
    translationKrate: Krate<Translation>
) : AuctionSortTranslationMapping {
    private val translation by translationKrate
    override fun translate(auctionSort: AuctionSort): StringDesc.Raw = when (auctionSort) {
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
