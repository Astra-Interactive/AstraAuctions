package ru.astrainteractive.astramarket.gui.api.market.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.market.model.AuctionSort

fun AuctionSort.toTranslation(translation: PluginTranslation): LocalizedText {
    return when (this) {
        is AuctionSort.Material -> translation.menu.sort.byMaterial
        is AuctionSort.Date -> translation.menu.sort.byDate
        is AuctionSort.Name -> translation.menu.sort.byName
        is AuctionSort.Price -> translation.menu.sort.byPrice
        is AuctionSort.Player -> translation.menu.sort.byPlayer
    }
}
