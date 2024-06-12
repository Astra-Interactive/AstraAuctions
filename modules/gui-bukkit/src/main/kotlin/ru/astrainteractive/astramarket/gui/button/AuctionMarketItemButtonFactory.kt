package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.button.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astramarket.gui.util.DurationExt.getTimeFormatted
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class AuctionMarketItemButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {
    fun render(
        index: Int,
        click: Click,
        auctionItem: MarketSlot
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(itemStackEncoder.toItemStack(auctionItem.item))
        .addLore(translation.auction.leftButton.component)
        .addLore(translation.auction.middleClick.component)
        .addLore(translation.auction.rightButton.component)
        .addLore {
            val owner = Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
            translation.auction.auctionBy(owner).component
        }
        .addLore {
            val time = auctionItem.time.milliseconds.getTimeFormatted(translation.general.timeAgoFormat).raw
            translation.auction.auctionCreatedAgo(time).component
        }
        .addLore {
            translation.auction.auctionPrice(auctionItem.price).component
        }
        .setOnClickListener(click)
        .build()
}
