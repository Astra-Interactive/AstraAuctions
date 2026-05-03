package ru.astrainteractive.astramarket.api

class SlotInventoryLayoutBuilder<K> {

    private val rows = mutableListOf<List<K>>()

    fun row(vararg keys: K) {
        rows += keys.toList()
    }

    fun row(size: Int, key: K) {
        rows += List(size) { key }
    }

    fun build(): SlotInventoryLayout<K> = SlotInventoryLayout(rows)
}

fun <K> slotInventoryLayout(
    block: SlotInventoryLayoutBuilder<K>.() -> Unit
): SlotInventoryLayout<K> = SlotInventoryLayoutBuilder<K>()
    .apply(block)
    .build()
