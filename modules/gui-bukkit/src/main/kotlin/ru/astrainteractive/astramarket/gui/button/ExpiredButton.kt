package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack

internal fun ButtonContext.expired(
    index: Int,
    isExpired: Boolean,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.expired.toItemStack())
    .editMeta {
        if (isExpired) {
            displayName(translation.menu.expired.component)
        } else {
            displayName(translation.menu.new.component)
        }
    }
    .setOnClickListener(click)
    .build()
