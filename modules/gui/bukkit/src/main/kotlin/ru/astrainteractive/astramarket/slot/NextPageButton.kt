package ru.astrainteractive.astramarket.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.context.ButtonContext
import ru.astrainteractive.astramarket.util.toItemStack

internal fun ButtonContext.nextPage(
    index: Int,
    click: Click
): InventorySlot {
    return InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(config.buttons.next.toItemStack())
        .editMeta { displayName(pluginTranslation.menu.nextPage.toComponent(locale)) }
        .setOnClickListener(click)
        .build()
}
