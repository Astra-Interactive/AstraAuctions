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
import ru.astrainteractive.astramarket.core.itemstack.ItemStackSerializer

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
    .setItemStack(ItemStackSerializer.decodeFromString(auctionItem.item).getOrThrow())
    .apply {
        if (hasExpirePermission) {
            addLore(pluginTranslation.auction.expireSlot.component)
        }
        addLore(pluginTranslation.auction.buySlot.component)
    }
    .apply {
        if (!isOwner && !hasRemovePermission) return@apply
        addLore(pluginTranslation.auction.removeSlot.component)
    }
    .addLore {
        val ownerName = auctionItem.minecraftUsername
            .takeIf(String::isNotEmpty)
            ?: UUID.fromString(auctionItem.minecraftUuid)
                .let(Bukkit::getOfflinePlayer)
                .name ?: "§kUNKNOWN"
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
