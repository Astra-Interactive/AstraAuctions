package ru.astrainteractive.astramarket.data.bridge

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.core.PluginPermission
import java.util.UUID

@Suppress("TooManyFunctions")
class BukkitAuctionsBridge(
    private val encoder: ObjectEncoder,
) : AuctionsBridge {
    private fun MarketSlot.itemStack(serializer: ObjectEncoder): ItemStack {
        return serializer.fromByteArray(item)
    }

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
        val item = marketSlot.itemStack(encoder)
        Bukkit.getPlayer(uuid)?.inventory?.addItem(item)
    }

    override suspend fun itemDesc(marketSlot: MarketSlot): String {
        return marketSlot.itemStack(encoder).displayNameOrMaterialName()
    }

    override fun playerName(uuid: UUID): String? {
        return Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
    }

    override fun hasExpirePermission(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.hasPermission(PluginPermission.Expire) ?: false
    }

    override fun isItemValid(marketSlot: MarketSlot): Boolean {
        val itemStack = marketSlot.itemStack(encoder)
        return itemStack != null && itemStack.type != Material.AIR
    }

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.maxPermissionSize(PluginPermission.SellMax)
    }
}
