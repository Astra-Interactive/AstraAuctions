package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack

internal fun ButtonContext.playersSlots(
    index: Int,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.playersSlots.toItemStack())
    .editMeta {
        displayName(pluginTranslation.menu.playerSlots.component)
    }
    .setOnClickListener(click)
    .build()

internal fun ButtonContext.allSlots(
    index: Int,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.aauc.toItemStack())
    .editMeta {
        displayName(pluginTranslation.menu.allSlots.component)
    }
    .setOnClickListener(click)
    .build()
