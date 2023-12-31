package ru.astrainteractive.astramarket.api.market

import ru.astrainteractive.astramarket.api.market.dto.MarketSlot

interface MarketApi {
    suspend fun insertSlot(marketSlot: MarketSlot): Int?
    suspend fun expireSlot(marketSlot: MarketSlot): Unit?
    suspend fun getUserSlots(uuid: String, expired: Boolean): List<MarketSlot>?
    suspend fun getSlots(expired: Boolean): List<MarketSlot>?
    suspend fun getSlotsOlderThan(millis: Long): List<MarketSlot>?
    suspend fun getSlot(id: Int): MarketSlot?
    suspend fun deleteSlot(marketSlot: MarketSlot): Unit?
    suspend fun countPlayerSlots(uuid: String): Int?
}
