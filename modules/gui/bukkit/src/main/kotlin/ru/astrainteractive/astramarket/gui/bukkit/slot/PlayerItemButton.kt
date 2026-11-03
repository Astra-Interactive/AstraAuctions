package ru.astrainteractive.astramarket.gui.bukkit.slot

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.addLore
import ru.astrainteractive.astralibs.menu.slot.editMeta
import ru.astrainteractive.astralibs.menu.slot.setIndex
import ru.astrainteractive.astralibs.menu.slot.setMaterial
import ru.astrainteractive.astralibs.menu.slot.setOnClickListener
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.gui.bukkit.context.api.ButtonContext
import ru.astrainteractive.astramarket.gui.bukkit.util.formatTime

internal fun ButtonContext.playerItem(
    index: Int,
    click: Click,
    isExpired: Boolean,
    playerAndSlots: PlayerAndSlots
): InventorySlot {
    return InventorySlot.Builder()
        .setIndex(index)
        .setMaterial(Material.PLAYER_HEAD)
        .editMeta {
            this as SkullMeta
            val offlinePlayer = Bukkit.getOfflinePlayer(playerAndSlots.minecraftUUID)
            if (!offlinePlayer.hasPlayedBefore()) return@editMeta
            owningPlayer = offlinePlayer
        }
        .editMeta {
            val ownerName = playerAndSlots.minecraftUsername
                .takeIf(String::isNotEmpty)
                ?: playerAndSlots.minecraftUUID
                    .let(Bukkit::getOfflinePlayer)
                    .name ?: "§kUNKNOWN"
            displayName(Component.text(ownerName))
        }
        .addLore {
            val auctionsAmount = playerAndSlots.slots.count { slot -> slot.expired == isExpired }
            pluginTranslation.menu.player.lots(auctionsAmount).toComponent(locale)
        }
        .addLore {
            val latestTime = playerAndSlots.slots.maxOf(MarketSlot::time)
            val time = clock.now().minus(latestTime).formatTime(pluginTranslation.menu.timeAgo)
            pluginTranslation.menu.player.latestLot(time).toComponent(locale)
        }
        .setOnClickListener(click)
        .build()
}
