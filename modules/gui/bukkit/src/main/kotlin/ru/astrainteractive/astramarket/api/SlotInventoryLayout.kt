package ru.astrainteractive.astramarket.api

import ru.astrainteractive.astralibs.menu.slot.InventorySlot

class SlotInventoryLayout<K>(
    private val slots: List<K>
) : InventoryLayout<K, InventorySlot> {

    override val size: Int get() = slots.size

    override fun keyAt(index: Int): K = slots[index]

    override fun indicesOf(key: K): List<Int> = slots.withIndex()
        .filter { indexedValue -> indexedValue.value == key }
        .map { indexedValue -> indexedValue.index }

    override fun firstIndexOf(key: K): Int = slots.indexOf(key)
        .takeIf { index -> index != -1 }
        ?: error("Key $key not found in layout")

    override fun count(key: K): Int = slots.count { slotKey -> slotKey == key }

    override fun mapSlotsNotNull(
        key: K,
        transform: (index: Int) -> InventorySlot?
    ): List<InventorySlot> = indicesOf(key).mapNotNull(transform)
}

fun <K> SlotInventoryLayout(rows: List<List<K>>): SlotInventoryLayout<K> {
    if (rows.isEmpty()) return SlotInventoryLayout(emptyList<K>())

    val width = rows.first().size
    require(rows.all { row -> row.size == width }) {
        "All rows must have the same width"
    }

    return SlotInventoryLayout(rows.flatten())
}
