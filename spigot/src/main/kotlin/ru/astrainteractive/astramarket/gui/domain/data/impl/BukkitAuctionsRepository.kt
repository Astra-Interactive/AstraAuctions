package ru.astrainteractive.astramarket.gui.domain.data.impl

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.itemStack
import ru.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astramarket.util.displayNameOrMaterialName
import java.util.UUID

class BukkitAuctionsRepository(
    private val dataSource: AuctionsAPI,
    private val serializer: Encoder,
    private val permissionManager: PermissionManager
) : AuctionsRepository {
    override suspend fun getAuctionOrNull(id: Int): AuctionDTO? {
        return dataSource.fetchAuction(id)
    }

    override suspend fun isInventoryFull(uuid: UUID): Boolean {
        val isFull = Bukkit.getPlayer(uuid)?.inventory?.firstEmpty() == -1
        return isFull
    }

    override suspend fun deleteAuction(auctionDTO: AuctionDTO): Unit? {
        return dataSource.deleteAuction(auctionDTO)
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
        return permissionManager.hasPermission(uuid, PluginPermission.Expire)
    }

    override suspend fun expireAuction(auctionDTO: AuctionDTO): Unit? {
        return dataSource.expireAuction(auctionDTO)
    }
}
