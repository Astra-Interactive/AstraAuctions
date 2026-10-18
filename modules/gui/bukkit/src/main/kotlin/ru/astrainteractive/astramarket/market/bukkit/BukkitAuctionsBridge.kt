package ru.astrainteractive.astramarket.market.bukkit

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.server.permission.asKPermissible
import ru.astrainteractive.astramarket.core.settings.model.PluginPermission
import ru.astrainteractive.astramarket.core.util.toItemStack
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.api.AuctionsBridge
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID

@Suppress("TooManyFunctions")
internal class BukkitAuctionsBridge(
    private val objectEncoder: ObjectEncoder,
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
        objectEncoder.toItemStack(marketSlot.item)
            .onFailure { error { "#addItemToInventory could not deserialize item" } }
            .onSuccess { itemStack -> Bukkit.getPlayer(uuid)?.inventory?.addItem(itemStack) }
    }

    override suspend fun itemDesc(marketSlot: MarketSlot): String {
        return objectEncoder.toItemStack(marketSlot.item)
            .onFailure { error { "#itemDesc could not deserialize item" } }
            .getOrNull()
            ?.displayNameOrMaterialName()
            .orEmpty()
    }

    override fun playerName(uuid: UUID): String? {
        return Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
    }

    override fun hasExpirePermission(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid)?.asKPermissible()?.hasPermission(PluginPermission.Expire) ?: false
    }

    override fun isItemValid(marketSlot: MarketSlot): Boolean {
        val itemStack = objectEncoder.toItemStack(marketSlot.item).getOrNull()
        return itemStack != null && itemStack.type != Material.AIR
    }

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? {
        return Bukkit.getPlayer(uuid)?.asKPermissible()?.maxPermissionSize(PluginPermission.SellMax)
    }
}
