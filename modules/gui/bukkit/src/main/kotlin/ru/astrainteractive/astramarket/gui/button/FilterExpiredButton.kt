package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.toItemStack

internal fun ButtonContext.filterExpired(
    index: Int,
    click: Click,
    isExpired: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.filterExpired.toItemStack())
    .setDisplayName(pluginTranslation.menu.filterExpired.component)
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
