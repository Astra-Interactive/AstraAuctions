package ru.astrainteractive.astramarket.data

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.plugin.PluginPermission
import java.util.UUID

@Suppress("TooManyFunctions")
class BukkitAuctionsBridge(
    private val marketApi: MarketApi,
    private val serializer: Encoder,
) : AuctionsBridge {
    private fun MarketSlot.itemStack(serializer: Encoder): ItemStack {
        return serializer.fromByteArray(item)
    }

    private fun ItemStack.displayNameOrMaterialName(): String {
        val name = itemMeta?.displayName
        if (name.isNullOrEmpty()) {
            return type.name
        }
        return name
    }

    override suspend fun getAuctionOrNull(id: Int): MarketSlot? {
        return marketApi.getSlot(id)
    }

    override suspend fun isInventoryFull(uuid: UUID): Boolean {
        val isFull = Bukkit.getPlayer(uuid)?.inventory?.firstEmpty() == -1
        return isFull
    }

    override suspend fun deleteAuction(marketSlot: MarketSlot): Unit? {
        return marketApi.deleteSlot(marketSlot)
    }

    override suspend fun addItemToInventory(marketSlot: MarketSlot, uuid: UUID) {
        val item = marketSlot.itemStack(serializer)
        Bukkit.getPlayer(uuid)?.inventory?.addItem(item)
    }

    override suspend fun itemDesc(marketSlot: MarketSlot): String {
        return marketSlot.itemStack(serializer).displayNameOrMaterialName()
    }

    override fun playerName(uuid: UUID): String? {
        return Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
    }

    override fun hasExpirePermission(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.hasPermission(PluginPermission.Expire) ?: false
    }

    override suspend fun expireAuction(marketSlot: MarketSlot): Unit? {
        return marketApi.expireSlot(marketSlot)
    }

    override fun isItemValid(marketSlot: MarketSlot): Boolean {
        val itemStack = marketSlot.itemStack(serializer)
        return itemStack != null && itemStack.type != Material.AIR
    }

    override suspend fun countPlayerAuctions(uuid: UUID): Int {
        return marketApi.countPlayerSlots(uuid.toString()) ?: 0
    }

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.maxPermissionSize(PluginPermission.SellMax)
    }

    override suspend fun insertAuction(marketSlot: MarketSlot): Int? {
        return marketApi.insertSlot(marketSlot)
    }
}
