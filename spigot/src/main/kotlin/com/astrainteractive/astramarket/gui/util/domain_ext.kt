@file:Suppress("Filename")

package com.astrainteractive.astramarket.gui.util

import com.astrainteractive.astramarket.gui.domain.models.AuctionSort
import com.astrainteractive.astramarket.plugin.Translation

fun AuctionSort.desc(translation: Translation) = when (this) {
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
