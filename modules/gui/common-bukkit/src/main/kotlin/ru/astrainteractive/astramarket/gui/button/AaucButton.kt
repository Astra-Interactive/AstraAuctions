package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.button.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack

internal fun ButtonContext.aauc(
    index: Int,
    click: Click,
    isExpired: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.aauc.toItemStack())
    .setDisplayName(pluginTranslation.menu.filter.component)
    .addLore {
        pluginTranslation.menu.enabledColor
            .takeIf { isExpired }
            .or { pluginTranslation.menu.disabledColor }
            .plus(pluginTranslation.menu.expired)
            .component
    }
    .addLore {
        pluginTranslation.menu.enabledColor
            .takeIf { !isExpired }
            .or { pluginTranslation.menu.disabledColor }
            .plus(pluginTranslation.menu.new)
            .component
    }
    .setOnClickListener(click)
    .build()

fun StringDesc?.orEmpty() = this ?: StringDesc.Raw("")
fun StringDesc?.or(block: () -> StringDesc) = this ?: block.invoke()
