package ru.astrainteractive.astramarket.presentation.invmap

import ru.astrainteractive.astralibs.menu.slot.InventorySlot

object InventoryMapExt {

    private val InventoryMap<*>.flatMap get() = map.flatMap { it.map { guiKey -> guiKey } }

    fun <T> InventoryMap<T>.countKeys(key: T): Int {
        return flatMap.count { it == key }
    }

    fun <T> InventoryMap<T>.withKeySlot(
        key: T,
        transform: (index: Int) -> InventorySlot?
    ): List<InventorySlot> {
        return flatMap
            .filter { entryGuiKey -> entryGuiKey == key }
            .mapIndexed { i, _ -> transform.invoke(i) }
            .filterNotNull()
    }

    fun <T> InventoryMap<T>.indexOf(key: T): Int {
        val i = flatMap.indexOf(key)
        if (i == -1) {
            error("Could not find $key in inventory map")
        }
        return i
    }
}
