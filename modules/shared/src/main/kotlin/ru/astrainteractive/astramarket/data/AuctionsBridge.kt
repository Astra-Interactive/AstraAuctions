package ru.astrainteractive.astramarket.data

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import java.util.UUID

@Suppress("TooManyFunctions")
interface AuctionsBridge {

    suspend fun getAuctionOrNull(id: Int): AuctionDTO?

    suspend fun isInventoryFull(uuid: UUID): Boolean

    suspend fun deleteAuction(auctionDTO: AuctionDTO): Unit?

    suspend fun addItemToInventory(auctionDTO: AuctionDTO, uuid: UUID)

    suspend fun itemDesc(auctionDTO: AuctionDTO): String

    fun playerName(uuid: UUID): String?

    fun hasExpirePermission(uuid: UUID): Boolean

    suspend fun expireAuction(auctionDTO: AuctionDTO): Unit?

    fun isItemValid(auctionDTO: AuctionDTO): Boolean

    suspend fun countPlayerAuctions(uuid: UUID): Int

    suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int?

    suspend fun insertAuction(auctionDTO: AuctionDTO): Int?
}
