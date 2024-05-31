package ru.astrainteractive.astramarket.api.market

import ru.astrainteractive.astramarket.api.market.model.MarketSlot

interface MarketApi {
    /**
     * Insert new [MarketSlot]
     * @return id of inserted slot
     * @throws Exception
     */
    suspend fun insertSlot(marketSlot: MarketSlot): Int?

    /**
     * Changes [MarketSlot.expired] to true
     * @throws Exception
     */
    suspend fun expireSlot(marketSlot: MarketSlot): Unit?

    /**
     * @return slots with specified [MarketSlot.minecraftUuid]
     * @throws Exception
     */
    suspend fun getUserSlots(uuid: String, expired: Boolean): List<MarketSlot>?

    /**
     * @return list of all slots
     * @throws Exception
     */
    suspend fun getSlots(expired: Boolean): List<MarketSlot>?

    /**
     * @return slots where [MarketSlot.time] more than [millis]
     * @throws Exception
     */
    suspend fun getSlotsOlderThan(millis: Long): List<MarketSlot>?

    /**
     * @return slot by it's [MarketSlot.id]
     * @throws Exception
     */
    suspend fun getSlot(id: Int): MarketSlot?

    /**
     * Deletes [MarketSlot]
     * @throws Exception
     */
    suspend fun deleteSlot(marketSlot: MarketSlot): Unit?

    /**
     * @return amount of all player slots
     * @throws Exception
     */
    suspend fun countPlayerSlots(uuid: String): Int?
}
