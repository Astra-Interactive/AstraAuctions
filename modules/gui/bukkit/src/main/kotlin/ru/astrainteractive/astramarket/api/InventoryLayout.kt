package ru.astrainteractive.astramarket.api

interface InventoryLayout<K, S> {
    val size: Int
    fun keyAt(index: Int): K
    fun indicesOf(key: K): List<Int>
    fun firstIndexOf(key: K): Int
    fun count(key: K): Int
    fun mapSlotsNotNull(
        key: K,
        transform: (index: Int) -> S?
    ): List<S>
}
