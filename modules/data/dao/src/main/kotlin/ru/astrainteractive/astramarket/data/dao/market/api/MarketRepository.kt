package ru.astrainteractive.astramarket.data.dao.market.api

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import kotlin.time.Instant

interface MarketRepository {
    suspend fun insertSlot(marketSlot: MarketSlot): Result<Int>

    suspend fun expireSlot(marketSlot: MarketSlot): Result<Unit>

    suspend fun loadUserSlots(uuid: String, isExpired: Boolean): Result<List<MarketSlot>>

    suspend fun loadSlots(isExpired: Boolean): Result<List<MarketSlot>>

    suspend fun loadSlotsCreatedBefore(moment: Instant): Result<List<MarketSlot>>

    suspend fun findSlot(id: Int): Result<MarketSlot?>

    suspend fun deleteSlot(marketSlot: MarketSlot): Result<Unit>

    suspend fun countPlayerSlots(uuid: String): Result<Int>
}
