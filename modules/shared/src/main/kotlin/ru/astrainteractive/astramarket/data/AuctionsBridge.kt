package ru.astrainteractive.astramarket.data

import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import java.util.UUID

@Suppress("TooManyFunctions")
interface AuctionsBridge {

    suspend fun getAuctionOrNull(id: Int): MarketSlot?

    suspend fun isInventoryFull(uuid: UUID): Boolean

    suspend fun deleteAuction(marketSlot: MarketSlot): Unit?

    suspend fun addItemToInventory(marketSlot: MarketSlot, uuid: UUID)

    suspend fun itemDesc(marketSlot: MarketSlot): String

    fun playerName(uuid: UUID): String?

    fun hasExpirePermission(uuid: UUID): Boolean

    suspend fun expireAuction(marketSlot: MarketSlot): Unit?

    fun isItemValid(marketSlot: MarketSlot): Boolean

    suspend fun countPlayerAuctions(uuid: UUID): Int

    suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int?

    suspend fun insertAuction(marketSlot: MarketSlot): Int?
}
