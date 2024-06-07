package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.util.DurationExt.getTimeFormatted
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class AuctionMarketItemButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {
    fun render(
        index: Int,
        click: Click,
        auctionItem: MarketSlot
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(objectEncoder.fromByteArray<ItemStack>(auctionItem.item))
        .editMeta {
            listOf(
                translation.auction.leftButton.let(kyoriComponentSerializer::toComponent),
                translation.auction.middleClick.let(kyoriComponentSerializer::toComponent),
                translation.auction.rightButton.let(kyoriComponentSerializer::toComponent),
                translation.auction.auctionBy.replace(
                    "%player_owner%",
                    Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
                ).let(kyoriComponentSerializer::toComponent),
                translation.auction.auctionCreatedAgo.replace(
                    "%time%",
                    auctionItem.time.milliseconds.getTimeFormatted(translation.general.timeAgoFormat).raw
                ).let(kyoriComponentSerializer::toComponent),
                translation.auction.auctionPrice.replace(
                    "%price%",
                    auctionItem.price.toString()
                ).let(kyoriComponentSerializer::toComponent),
            ).run(::lore)
        }
        .setOnClickListener(click)
        .build()
}
