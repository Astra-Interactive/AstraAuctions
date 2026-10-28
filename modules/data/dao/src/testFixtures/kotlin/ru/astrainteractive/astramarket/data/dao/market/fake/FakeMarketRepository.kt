package ru.astrainteractive.astramarket.data.dao.market.fake

import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.MarketStorageError
import kotlin.time.Instant

class FakeMarketRepository(
    slots: List<MarketSlot>
) : MarketRepository {
    private val slotsById = slots.associateBy(MarketSlot::id).toMutableMap()

    val failingOperations = mutableSetOf<Operation>()

    val slots: List<MarketSlot>
        get() = slotsById.values.toList()

    private fun <T> respond(operation: Operation, value: () -> T): Result<T> {
        if (operation !in failingOperations) return Result.success(value.invoke())
        return Result.failure(MarketStorageError.QueryFailed(IllegalStateException("$operation failed")))
    }

    override suspend fun insertSlot(marketSlot: MarketSlot): Result<Int> {
        return respond(Operation.INSERT) {
            val id = (slotsById.keys.maxOrNull() ?: 0) + 1
            slotsById[id] = marketSlot.copy(id = id)
            id
        }
    }

    override suspend fun expireSlot(marketSlot: MarketSlot): Result<Unit> {
        return respond(Operation.EXPIRE) {
            slotsById[marketSlot.id] = marketSlot.copy(expired = true)
        }
    }

    override suspend fun loadUserSlots(uuid: String, isExpired: Boolean): Result<List<MarketSlot>> {
        return respond(Operation.READ) {
            slotsById.values.filter { slot -> slot.minecraftUuid == uuid && slot.expired == isExpired }
        }
    }

    override suspend fun loadSlots(isExpired: Boolean): Result<List<MarketSlot>> {
        return respond(Operation.READ) {
            slotsById.values.filter { slot -> slot.expired == isExpired }
        }
    }

    override suspend fun loadSlotsCreatedBefore(moment: Instant): Result<List<MarketSlot>> {
        return respond(Operation.READ) {
            slotsById.values.filter { slot -> slot.time < moment }
        }
    }

    override suspend fun findSlot(id: Int): Result<MarketSlot?> {
        return respond(Operation.READ) { slotsById[id] }
    }

    override suspend fun deleteSlot(marketSlot: MarketSlot): Result<Unit> {
        return respond(Operation.DELETE) {
            slotsById.remove(marketSlot.id)
        }
    }

    override suspend fun countPlayerSlots(uuid: String): Result<Int> {
        return respond(Operation.READ) {
            slotsById.values.count { slot -> slot.minecraftUuid == uuid }
        }
    }

    enum class Operation {
        INSERT,
        EXPIRE,
        READ,
        DELETE
    }
}
