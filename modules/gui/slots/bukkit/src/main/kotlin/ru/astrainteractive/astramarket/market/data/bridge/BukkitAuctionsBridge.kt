package ru.astrainteractive.astramarket.market.data.bridge

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID

@Suppress("TooManyFunctions")
internal class BukkitAuctionsBridge(
    private val itemStackEncoder: ItemStackEncoder,
) : AuctionsBridge, Logger by JUtiltLogger("AstraMarket-BukkitAuctionsBridge").withoutParentHandlers() {

    private fun ItemStack.displayNameOrMaterialName(): String {
        val name = itemMeta?.displayName
        if (name.isNullOrEmpty()) {
            return type.name
        }
        return name
    }

    override suspend fun isInventoryFull(uuid: UUID): Boolean {
        val isFull = Bukkit.getPlayer(uuid)?.inventory?.firstEmpty() == -1
        return isFull
    }

    override suspend fun addItemToInventory(marketSlot: MarketSlot, uuid: UUID) {
        itemStackEncoder.toItemStack(marketSlot.item)
            .onFailure { error { "#addItemToInventory could not deserialize item" } }
            .onSuccess { itemStack -> Bukkit.getPlayer(uuid)?.inventory?.addItem(itemStack) }
    }

    override suspend fun itemDesc(marketSlot: MarketSlot): String {
        return itemStackEncoder.toItemStack(marketSlot.item)
            .onFailure { error { "#itemDesc could not deserialize item" } }
            .getOrNull()
            ?.displayNameOrMaterialName()
            .orEmpty()
    }

    override fun playerName(uuid: UUID): String? {
        return Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
    }

    override fun hasExpirePermission(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.hasPermission(PluginPermission.Expire) ?: false
    }

    override fun isItemValid(marketSlot: MarketSlot): Boolean {
        val itemStack = itemStackEncoder.toItemStack(marketSlot.item).getOrNull()
        return itemStack != null && itemStack.type != Material.AIR
    }

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.maxPermissionSize(PluginPermission.SellMax)
    }
}
