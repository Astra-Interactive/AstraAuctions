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
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort

internal fun ButtonContext.auctionSort(
    index: Int,
    sortType: AuctionSort,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.sort.toItemStack())
    .setDisplayName(pluginTranslation.menu.sort.component)
    .apply {
        listOf(
            AuctionSort.Date(false),
            AuctionSort.Material(false),
            AuctionSort.Name(false),
            AuctionSort.Price(false),
            AuctionSort.Player(false),
        ).forEach { entry ->
            addLore {
                val symbol = pluginTranslation.auction.sortAscSymbol
                    .takeIf { sortType.isAsc }
                    .or { pluginTranslation.auction.sortDescSymbol }
                    .takeIf { sortType::class == entry::class }
                    .orEmpty()
                pluginTranslation.menu.enabledColor
                    .takeIf { sortType::class == entry::class }
                    .or { pluginTranslation.menu.disabledColor }
                    .plus(auctionSortTranslationMapping.translate(entry))
                    .plus(symbol)
                    .component
            }
        }
    }
    .setOnClickListener(click)
    .build()
