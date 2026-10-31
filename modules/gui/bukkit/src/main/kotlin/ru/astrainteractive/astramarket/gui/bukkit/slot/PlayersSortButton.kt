package ru.astrainteractive.astramarket.gui.bukkit.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort
import ru.astrainteractive.astramarket.gui.api.player.util.toTranslation
import ru.astrainteractive.astramarket.gui.bukkit.context.api.ButtonContext
import ru.astrainteractive.astramarket.gui.bukkit.util.toItemStack

internal fun ButtonContext.playersSort(
    index: Int,
    sortType: PlayerSort,
    click: Click
): InventorySlot {
    return InventorySlot.Builder()
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
                        label = entry.toTranslation(pluginTranslation),
                        isSelected = isSelected,
                        isAsc = sortType.isAsc
                    ).toComponent(locale)
                }
            }
        }
        .setOnClickListener(click)
        .build()
}
