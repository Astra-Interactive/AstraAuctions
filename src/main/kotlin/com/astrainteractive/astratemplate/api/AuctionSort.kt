package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.utils.Translation

enum class AuctionSort(val desc: String) {
    MATERIAL_DESC(Translation.sortMaterialDesc),
    MATERIAL_ASC(Translation.sortMaterialAsc),
    DATE_DESC(Translation.sortDateDesc),
    DATE_ASC(Translation.sortDateAsc),
    NAME_DESC(Translation.sortNameDesc),
    NAME_ASC(Translation.sortNameAsc),
    PRICE_DESC(Translation.sortPriceDesc),
    PRICE_ASC(Translation.sortPriceAsc),
    PLAYER_ASC(Translation.sortPlayerAsc),
    PLAYER_DESC(Translation.sortPlayerDesc);
}

