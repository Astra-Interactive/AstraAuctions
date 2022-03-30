package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.utils.Translation

enum class SortType(val desc: String) {
    MATERIAL_DESC(Translation.sortMaterialDesc),
    MATERIAL_ASC(Translation.sortMaterialAsc),
    DATE_DESC(Translation.sortDateDesc),
    DATE_ASC(Translation.sortDateAsc),
    NAME_DESC(Translation.sortNameDesc),
    NAME_ASC(Translation.sortNameAsc),
    PRICE_DESC(Translation.sortPriceDesc),
    PRICE_ASC(Translation.sortPriceAsc),
    PLAYER_ASC(Translation.sortPlayerAsc),
    PLAYER_DESC(Translation.sortPlayerDesc)

}
fun SortType.next() = getNextByInt(1)
fun SortType.prev() = getNextByInt(-1)
fun SortType.getNextByInt(add:Int): SortType {
    val i = SortType.values().indexOf(this)+add
    if (i >= SortType.values().size)
        return SortType.values().first()
    else if (i < 0)
        return SortType.values().last()
    return SortType.values()[i]
}