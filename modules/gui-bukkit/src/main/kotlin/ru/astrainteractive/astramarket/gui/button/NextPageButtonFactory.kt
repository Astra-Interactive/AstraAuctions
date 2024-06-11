package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.toItemStack

internal class NextPageButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {

    fun render(
        index: Int,
        click: Click
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(config.buttons.next.toItemStack())
        .editMeta { displayName(translation.menu.next.component) }
        .setOnClickListener(click)
        .build()
}
