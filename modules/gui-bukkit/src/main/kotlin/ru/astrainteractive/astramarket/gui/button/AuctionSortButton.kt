package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort

internal fun ButtonContext.auctionSort(
    index: Int,
    sortType: AuctionSort,
    click: Click
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(config.buttons.sort.toItemStack())
    .editMeta {
        val sortDesc = auctionSortTranslationMapping.translate(sortType).raw
        val desc = StringDesc.Raw("${translation.menu.sort.raw} $sortDesc")
        displayName(desc.component)
    }
    .setOnClickListener(click)
    .build()
