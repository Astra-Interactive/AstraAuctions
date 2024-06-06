package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.domain.model.AuctionSort
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack

class SortButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {

    fun render(
        index: Int,
        sortType: AuctionSort,
        click: Click
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(config.buttons.expired.toItemStack())
        .editMeta {
            val sortDesc = sortTranslationMapping.translate(sortType).raw
            val desc = StringDesc.Raw("${translation.menu.sort.raw} $sortDesc")
            displayName(desc.component)
        }
        .setOnClickListener(click)
        .build()
}
