package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.toItemStack

internal fun ButtonContext.border(index: Int) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.border.toItemStack())
    .setDisplayName(" ")
    .build()
