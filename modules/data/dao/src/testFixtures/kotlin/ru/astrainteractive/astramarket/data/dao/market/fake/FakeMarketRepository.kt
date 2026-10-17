package ru.astrainteractive.astramarket.data.dao.market.fake

import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.MarketStorageError

class FakeMarketRepository(
    slots: List<MarketSlot>
) : MarketRepository {
    private val slotsById = slots.associateBy(MarketSlot::id).toMutableMap()

    var failure: Throwable? = null

    val slots: List<MarketSlot>
        get() = slotsById.values.toList()

    private fun <T> respond(value: () -> T): Result<T> {
        val currentFailure = failure ?: return Result.success(value.invoke())
        return Result.failure(MarketStorageError.QueryFailed(currentFailure))
    }

    override suspend fun insertSlot(marketSlot: MarketSlot): Result<Int> = respond {
        val id = (slotsById.keys.maxOrNull() ?: 0) + 1
        slotsById[id] = marketSlot.copy(id = id)
        id
    }

    override suspend fun expireSlot(marketSlot: MarketSlot): Result<Unit> = respond {
        slotsById[marketSlot.id] = marketSlot.copy(expired = true)
    }

    override suspend fun getUserSlots(uuid: String, isExpired: Boolean): Result<List<MarketSlot>> = respond {
        slotsById.values.filter { slot -> slot.minecraftUuid == uuid && slot.expired == isExpired }
    }

    override suspend fun getSlots(isExpired: Boolean): Result<List<MarketSlot>> = respond {
        slotsById.values.filter { slot -> slot.expired == isExpired }
    }

    override suspend fun getSlotsOlderThan(millis: Long): Result<List<MarketSlot>> = respond {
        val time = System.currentTimeMillis() - millis
        slotsById.values.filter { slot -> slot.time < time }
    }

    override suspend fun getSlot(id: Int): Result<MarketSlot?> = respond { slotsById[id] }

    override suspend fun deleteSlot(marketSlot: MarketSlot): Result<Unit> = respond {
        slotsById.remove(marketSlot.id)
    }

    override suspend fun countPlayerSlots(uuid: String): Result<Int> = respond {
        slotsById.values.count { slot -> slot.minecraftUuid == uuid }
    }
}
