package ru.astrainteractive.astramarket.market.domain.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

interface AuctionSortTranslationMapping {
    fun translate(auctionSort: AuctionSort): StringDesc.Raw
}

internal class AuctionSortTranslationMappingImpl(
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) : AuctionSortTranslationMapping {
    private val translation by pluginTranslationKrate

    override fun translate(auctionSort: AuctionSort): StringDesc.Raw = when (auctionSort) {
        is AuctionSort.Material -> translation.auction.sortMaterial
        is AuctionSort.Date -> translation.auction.sortDate
        is AuctionSort.Name -> translation.auction.sortName
        is AuctionSort.Price -> translation.auction.sortPrice
        is AuctionSort.Player -> translation.auction.sortPlayer
    }
}
