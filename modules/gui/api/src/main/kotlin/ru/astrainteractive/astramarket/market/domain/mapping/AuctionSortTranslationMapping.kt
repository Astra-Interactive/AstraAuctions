package ru.astrainteractive.astramarket.market.domain.mapping

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

interface AuctionSortTranslationMapping {
    fun translate(auctionSort: AuctionSort): LocalizedText
}

internal class AuctionSortTranslationMappingImpl(
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) : AuctionSortTranslationMapping {
    private val translation by pluginTranslationKrate

    override fun translate(auctionSort: AuctionSort): LocalizedText = when (auctionSort) {
        is AuctionSort.Material -> translation.menu.sort.byMaterial
        is AuctionSort.Date -> translation.menu.sort.byDate
        is AuctionSort.Name -> translation.menu.sort.byName
        is AuctionSort.Price -> translation.menu.sort.byPrice
        is AuctionSort.Player -> translation.menu.sort.byPlayer
    }
}
