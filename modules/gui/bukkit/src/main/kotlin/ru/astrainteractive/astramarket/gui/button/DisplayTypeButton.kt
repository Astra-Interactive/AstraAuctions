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

internal fun ButtonContext.slotsType(
    index: Int,
    click: Click,
    isGroupedByPlayers: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.slotsType.toItemStack())
    .setDisplayName(pluginTranslation.menu.displayType.toComponent(locale))
    .addLore {
        optionColor(isSelected = isGroupedByPlayers)
            .concat(pluginTranslation.menu.playerSlots)
            .toComponent(locale)
    }
    .addLore {
        optionColor(isSelected = !isGroupedByPlayers)
            .concat(pluginTranslation.menu.allSlots)
            .toComponent(locale)
    }
    .setOnClickListener(click)
    .build()
