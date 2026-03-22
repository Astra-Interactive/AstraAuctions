package ru.astrainteractive.astramarket.market.data.bridge

import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import java.util.UUID

@Suppress("TooManyFunctions")
interface AuctionsBridge {

    suspend fun isInventoryFull(uuid: UUID): Boolean

    suspend fun addItemToInventory(marketSlot: MarketSlot, uuid: UUID)

    suspend fun itemDesc(marketSlot: MarketSlot): String

    suspend fun maxAllowedAuctionsForPlayer(uuid: UUID): Int?

    fun playerName(uuid: UUID): String?

    fun hasExpirePermission(uuid: UUID): Boolean

    fun isItemValid(marketSlot: MarketSlot): Boolean
}
