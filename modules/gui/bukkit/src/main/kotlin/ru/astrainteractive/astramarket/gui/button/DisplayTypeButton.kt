package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astralibs.string.plus
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.toItemStack

internal fun ButtonContext.slotsType(
    index: Int,
    click: Click,
    isGroupedByPlayers: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.slotsType.toItemStack())
    .setDisplayName(pluginTranslation.menu.displayType.component)
    .addLore {
        pluginTranslation.menu.enabledColor
            .takeIf { isGroupedByPlayers }
            .or { pluginTranslation.menu.disabledColor }
            .plus(pluginTranslation.menu.playerSlots)
            .component
    }
    .addLore {
        pluginTranslation.menu.enabledColor
            .takeIf { !isGroupedByPlayers }
            .or { pluginTranslation.menu.disabledColor }
            .plus(pluginTranslation.menu.allSlots)
            .component
    }
    .setOnClickListener(click)
    .build()
