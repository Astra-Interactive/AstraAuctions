package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.button.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astramarket.gui.util.DurationExt.getTimeFormatted
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class ExpiredMarketItemButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {

    fun render(
        index: Int,
        click: Click,
        auctionItem: MarketSlot
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(objectEncoder.fromByteArray(auctionItem.item))
        .addLore(translation.auction.rightButton.component)
        .addLore {
            val ownerUuid = UUID.fromString(auctionItem.minecraftUuid)
            val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
            translation.auction.auctionBy.replace("%player_owner%", ownerName).component
        }
        .addLore {
            val time = auctionItem.time.milliseconds.getTimeFormatted(translation.general.timeAgoFormat).raw
            translation.auction.auctionCreatedAgo.replace("%time%", time).component
        }
        .addLore {
            val price = auctionItem.price.toString()
            translation.auction.auctionPrice.replace("%price%", price).component
        }
        .setOnClickListener(click)
        .build()
}
