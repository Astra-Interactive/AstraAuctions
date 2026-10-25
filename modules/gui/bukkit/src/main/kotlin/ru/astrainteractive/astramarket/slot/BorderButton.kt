package ru.astrainteractive.astramarket.slot

import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astramarket.context.ButtonContext
import ru.astrainteractive.astramarket.util.toItemStack

internal fun ButtonContext.border(index: Int): InventorySlot {
    return InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(config.buttons.border.toItemStack())
        .setDisplayName(" ")
        .build()
}
