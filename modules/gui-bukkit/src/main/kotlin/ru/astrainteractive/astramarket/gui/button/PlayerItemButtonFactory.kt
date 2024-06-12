package ru.astrainteractive.astramarket.gui.button

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.meta.SkullMeta
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setMaterial
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astramarket.api.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.gui.button.di.ButtonFactoryDependency
import ru.astrainteractive.astramarket.gui.button.util.InventorySlotBuilderExt.addLore
import ru.astrainteractive.astramarket.gui.util.DurationExt.getTimeFormatted
import kotlin.time.Duration.Companion.milliseconds

internal class PlayerItemButtonFactory(
    dependency: ButtonFactoryDependency
) : ButtonFactoryDependency by dependency,
    KyoriComponentSerializer by dependency.kyoriComponentSerializer {

    fun render(
        index: Int,
        click: Click,
        isExpired: Boolean,
        playerAndSlots: PlayerAndSlots
    ) = InventorySlot.Builder()
        .setIndex(index)
        .setMaterial(Material.PLAYER_HEAD)
        .editMeta {
            this as SkullMeta
            val offlinePlayer = Bukkit.getOfflinePlayer(playerAndSlots.minecraftUUID)
            if (!offlinePlayer.hasPlayedBefore()) return@editMeta
            owningPlayer = offlinePlayer
        }
        .editMeta {
            val ownerUuid = playerAndSlots.minecraftUUID
            val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
            displayName(kyoriComponentSerializer.toComponent(ownerName))
        }
        .addLore {
            val auctionsAmount = playerAndSlots.slots
                .filter { it.expired == isExpired }
                .size
            translation.auction.auctionsAmount(auctionsAmount).component
        }
        .addLore {
            val time = playerAndSlots.slots
                .maxBy { it.time }
                .time.milliseconds
                .getTimeFormatted(translation.general.timeAgoFormat).raw
            translation.auction.auctionLast(time).component
        }
        .setOnClickListener(click)
        .build()
}
