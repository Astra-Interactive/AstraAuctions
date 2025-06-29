package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.button.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astramarket.gui.util.DurationExt.getTimeFormatted
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Suppress("LongParameterList")
internal fun ButtonContext.expiredSlot(
    index: Int,
    click: Click,
    auctionItem: MarketSlot,
    isOwner: Boolean,
    hasExpirePermission: Boolean,
    hasRemovePermission: Boolean
) = InventorySlot.Builder()
    .setIndex(index)
    .setItemStack(itemStackEncoder.toItemStack(auctionItem.item))
    .apply {
        if (hasExpirePermission) addLore(pluginTranslation.auction.expireSlot.component)
        if (hasRemovePermission || (auctionItem.expired && isOwner)) {
            addLore(pluginTranslation.auction.removeSlot.component)
        }
        addLore(pluginTranslation.auction.buySlot.component)
    }
    .apply {
        if (!isOwner) return@apply
        addLore(pluginTranslation.auction.removeSlot.component)
    }
    .addLore {
        val ownerUuid = UUID.fromString(auctionItem.minecraftUuid)
        val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
        pluginTranslation.auction.auctionBy(ownerName).component
    }
    .addLore {
        val time = auctionItem.time.milliseconds.getTimeFormatted(pluginTranslation.general.timeAgoFormat).raw
        pluginTranslation.auction.auctionCreatedAgo(time).component
    }
    .addLore {
        pluginTranslation.auction.auctionPrice(auctionItem.price).component
    }
    .setOnClickListener(click)
    .build()
