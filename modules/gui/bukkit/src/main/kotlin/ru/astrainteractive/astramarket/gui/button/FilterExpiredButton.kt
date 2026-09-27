package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.toItemStack

internal fun ButtonContext.filterExpired(
    index: Int,
    click: Click,
    isExpired: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.filterExpired.toItemStack())
    .setDisplayName(pluginTranslation.menu.filterExpired.toComponent(locale))
    .addLore {
        optionColor(isSelected = isExpired)
            .concat(pluginTranslation.menu.expired)
            .toComponent(locale)
    }
    .addLore {
        optionColor(isSelected = !isExpired)
            .concat(pluginTranslation.menu.new)
            .toComponent(locale)
    }
    .setOnClickListener(click)
    .build()
