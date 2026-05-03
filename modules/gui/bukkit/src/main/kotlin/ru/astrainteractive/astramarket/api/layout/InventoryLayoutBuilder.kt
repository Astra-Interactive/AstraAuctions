package ru.astrainteractive.astramarket.api.layout

import ru.astrainteractive.astralibs.menu.slot.InventorySlot

class InventoryLayoutBuilder<KEY, SLOT> {

    private val rows = mutableListOf<List<KEY>>()

    fun row(vararg keys: KEY) {
        rows += keys.toList()
    }

    fun row(size: Int, key: KEY) {
        rows += List(size) { key }
    }

    fun build(): InventoryLayout<KEY, SLOT> = DefaultInventoryLayout(rows)
}

fun <KEY, SLOT> inventoryLayout(
    block: InventoryLayoutBuilder<KEY, SLOT>.() -> Unit
): InventoryLayout<KEY, SLOT> = InventoryLayoutBuilder<KEY, SLOT>()
    .apply(block)
    .build()

fun <KEY> slotInventoryLayout(
    block: InventoryLayoutBuilder<KEY, InventorySlot>.() -> Unit
) = inventoryLayout(block)
