package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.utils.Translation

enum class SortType(val desc: String) {
    MATERIAL_DESC(Translation.instance.sortMaterialDesc),
    MATERIAL_ASC(Translation.instance.sortMaterialAsc),
    DATE_DESC(Translation.instance.sortDateDesc),
    DATE_ASC(Translation.instance.sortDateAsc),
    NAME_DESC(Translation.instance.sortNameDesc),
    NAME_ASC(Translation.instance.sortNameAsc),
    PRICE_DESC(Translation.instance.sortPriceDesc),
    PRICE_ASC(Translation.instance.sortPriceAsc),
    PLAYER_ASC(Translation.instance.sortPlayerAsc),
    PLAYER_DESC(Translation.instance.sortPlayerDesc)

}
fun SortType.next() = getNextByInt(1)
fun SortType.prev() = getNextByInt(-1)
fun SortType.getNextByInt(add:Int): SortType {
    println("Index ${SortType.values().indexOf(this)}")
    val i = SortType.values().indexOf(this)+add
    if (i >= SortType.values().size)
        return SortType.values().first()
    else if (i < 0)
        return SortType.values().last()
    return SortType.values()[i]
}