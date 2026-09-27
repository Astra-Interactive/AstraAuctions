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
import ru.astrainteractive.astramarket.players.model.PlayerSort

internal fun ButtonContext.playersSort(
    index: Int,
    sortType: PlayerSort,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.sort.toItemStack())
    .setDisplayName(pluginTranslation.menu.sort.title.toComponent(locale))
    .apply {
        listOf(
            PlayerSort.Name(false),
            PlayerSort.Auctions(false),
        ).forEach { entry ->
            addLore {
                val isSelected = sortType::class == entry::class
                sortOption(
                    label = playersSortTranslationMapping.translate(entry),
                    isSelected = isSelected,
                    isAsc = sortType.isAsc
                ).toComponent(locale)
            }
        }
    }
    .setOnClickListener(click)
    .build()
