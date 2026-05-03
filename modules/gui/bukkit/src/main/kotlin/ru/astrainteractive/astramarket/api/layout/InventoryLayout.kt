package ru.astrainteractive.astramarket.api.layout

interface InventoryLayout<KEY, SLOT> {
    val size: Int
    fun keyAt(index: Int): KEY
    fun indicesOf(key: KEY): List<Int>
    fun firstIndexOf(key: KEY): Int
    fun count(key: KEY): Int
    fun mapSlotsNotNull(
        key: KEY,
        transform: (index: Int) -> SLOT?
    ): List<SLOT>
}
