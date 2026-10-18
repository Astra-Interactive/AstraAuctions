package ru.astrainteractive.astramarket.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.context.ButtonContext
import ru.astrainteractive.astramarket.util.toItemStack

internal fun ButtonContext.prevPage(
    index: Int,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.previous.toItemStack())
    .editMeta { displayName(pluginTranslation.menu.previousPage.toComponent(locale)) }
    .setOnClickListener(click)
    .build()
