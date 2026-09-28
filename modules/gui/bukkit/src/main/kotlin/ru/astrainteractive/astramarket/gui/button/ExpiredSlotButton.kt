package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setItemStack
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.util.getTimeFormatted
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Suppress("LongParameterList")
internal fun ButtonContext.expiredSlot(
    index: Int,
    click: Click,
    auctionItem: MarketSlot,
    isOwner: Boolean,
    hasExpirePermission: Boolean,
    hasRemovePermission: Boolean
): InventorySlot? {
    val itemStack = itemStackEncoder.toItemStack(auctionItem.item)
        .onFailure { error { "#expiredSlot could not deserialize item" } }
        .getOrNull()
        ?: return null
    return InventorySlot.Builder()
        .setIndex(index)
        .setItemStack(itemStack)
        .apply {
            if (hasExpirePermission) {
                addLore(pluginTranslation.menu.lot.expireHint.toComponent(locale))
            }
            addLore(pluginTranslation.menu.lot.buyHint.toComponent(locale))
        }
        .apply {
            if (!isOwner && !hasRemovePermission) return@apply
            addLore(pluginTranslation.menu.lot.removeHint.toComponent(locale))
        }
        .addLore {
            val ownerName = auctionItem.minecraftUsername
                .takeIf(String::isNotEmpty)
                ?: UUID.fromString(auctionItem.minecraftUuid)
                    .let(Bukkit::getOfflinePlayer)
                    .name ?: "§kUNKNOWN"
            pluginTranslation.menu.lot.seller(ownerName).toComponent(locale)
        }
        .addLore {
            val time = auctionItem.time.milliseconds.getTimeFormatted(pluginTranslation.menu.timeAgo)
            pluginTranslation.menu.lot.listed(time).toComponent(locale)
        }
        .addLore {
            pluginTranslation.menu.lot.price(auctionItem.price).toComponent(locale)
        }
        .setOnClickListener(click)
        .build()
}
