package ru.astrainteractive.astramarket.data

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.plugin.PluginPermission
import java.util.UUID

@Suppress("TooManyFunctions")
class BukkitAuctionsBridge(
    private val auctionsApi: AuctionsAPI,
    private val serializer: Encoder,
) : AuctionsBridge {
    private fun AuctionDTO.itemStack(serializer: Encoder): ItemStack {
        return serializer.fromByteArray(item)
    }

    private fun ItemStack.displayNameOrMaterialName(): String {
        val name = itemMeta?.displayName
        if (name.isNullOrEmpty()) {
            return type.name
        }
        return name
    }

    override suspend fun getAuctionOrNull(id: Int): AuctionDTO? {
        return auctionsApi.fetchAuction(id)
    }

    override suspend fun isInventoryFull(uuid: UUID): Boolean {
        val isFull = Bukkit.getPlayer(uuid)?.inventory?.firstEmpty() == -1
        return isFull
    }

    override suspend fun deleteAuction(auctionDTO: AuctionDTO): Unit? {
        return auctionsApi.deleteAuction(auctionDTO)
    }

    override suspend fun addItemToInventory(auctionDTO: AuctionDTO, uuid: UUID) {
        val item = auctionDTO.itemStack(serializer)
        Bukkit.getPlayer(uuid)?.inventory?.addItem(item)
    }

    override suspend fun itemDesc(auctionDTO: AuctionDTO): String {
        return auctionDTO.itemStack(serializer).displayNameOrMaterialName()
    }

    override fun playerName(uuid: UUID): String? {
        return Bukkit.getOfflinePlayer(uuid).name ?: Bukkit.getPlayer(uuid)?.name
    }

    override fun hasExpirePermission(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.hasPermission(PluginPermission.Expire) ?: false
    }

    override suspend fun expireAuction(auctionDTO: AuctionDTO): Unit? {
        return auctionsApi.expireAuction(auctionDTO)
    }

    override fun isItemValid(auctionDTO: AuctionDTO): Boolean {
        val itemStack = auctionDTO.itemStack(serializer)
        return itemStack != null && itemStack.type != Material.AIR
    }

    override suspend fun countPlayerAuctions(uuid: UUID): Int {
        return auctionsApi.countPlayerAuctions(uuid.toString()) ?: 0
    }

    override suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int? {
        return Bukkit.getPlayer(uuid)?.toPermissible()?.maxPermissionSize(PluginPermission.SellMax)
    }

    override suspend fun insertAuction(auctionDTO: AuctionDTO): Int? {
        return auctionsApi.insertAuction(auctionDTO)
    }
}
