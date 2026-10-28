package ru.astrainteractive.astramarket.slot

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.context.ButtonContext
import ru.astrainteractive.astramarket.market.model.AuctionSort
import ru.astrainteractive.astramarket.market.util.toTranslation
import ru.astrainteractive.astramarket.util.toItemStack

internal fun ButtonContext.auctionSort(
    index: Int,
    sortType: AuctionSort,
    click: Click
): InventorySlot {
    return InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(config.buttons.sort.toItemStack())
        .setDisplayName(pluginTranslation.menu.sort.title.toComponent(locale))
        .apply {
            listOf(
                AuctionSort.Date(false),
                AuctionSort.Material(false),
                AuctionSort.Name(false),
                AuctionSort.Price(false),
                AuctionSort.Player(false),
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
