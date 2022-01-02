package com.astrainteractive.astratemplate.api

enum class SortType(val desc: String) {
    MATERIAL_DESC("по материалу (обратный)"),
    MATERIAL_ASC("по материалу"),
    DATE_ASC("по дате"),
    DATE_DESC("по дате (обратный)"),
    NAME_DESC("по имени (обратный)"),
    NAME_ASC("по имени"), PRICE_DESC("по цене (обратный)"), PRICE_ASC("по цене")

}
fun SortType.next() = getNextByInt(1)
fun SortType.prev() = getNextByInt(-1)
fun SortType.getNextByInt(add:Int): SortType {
    val i = SortType.values().indexOf(this)+add
    if (i >= SortType.values().size)
        return SortType.values().first()
    else if (i <= 0)
        return SortType.values().last()
    return SortType.values()[i]
}