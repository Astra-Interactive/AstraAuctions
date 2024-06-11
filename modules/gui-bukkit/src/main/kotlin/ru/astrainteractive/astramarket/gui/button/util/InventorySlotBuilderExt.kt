package ru.astrainteractive.astramarket.gui.button.util

import net.kyori.adventure.text.Component
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore

object InventorySlotBuilderExt {
    fun InventorySlot.Builder.addLore(component: () -> Component): InventorySlot.Builder {
        return addLore(component.invoke())
    }
}
