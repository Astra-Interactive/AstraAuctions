package ru.astrainteractive.astramarket.data.exposed.market

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.MarketStorageError
import ru.astrainteractive.astramarket.data.exposed.table.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.time.Instant

internal class ExposedMarketRepository(
    private val databaseFlow: Flow<Database>,
    private val dispatchers: KotlinDispatchers
) : MarketRepository,
    Logger by JUtiltLogger("AstraMarket-ExposedMarketRepository").withoutParentHandlers() {
    private val mutex = Mutex()

    private suspend fun <T> query(tag: String, statement: JdbcTransaction.() -> T): Result<T> {
        return try {
            val value = mutex.withLock {
                withContext(dispatchers.IO) {
                    transaction(databaseFlow.first()) { statement.invoke(this) }
                }
            }
            Result.success(value)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            error(throwable) { "$tag: Error during execution" }
            Result.failure(MarketStorageError.QueryFailed(throwable))
        }
    }

    private fun toMarketSlot(resultRow: ResultRow): MarketSlot {
        return MarketSlot(
            id = resultRow[AuctionTable.id].value,
            time = Instant.fromEpochMilliseconds(resultRow[AuctionTable.time]),
            minecraftUuid = resultRow[AuctionTable.minecraftUuid],
            minecraftUsername = resultRow[AuctionTable.minecraftUsername],
            item = EncodedObject.ByteArray(resultRow[AuctionTable.item]),
            price = resultRow[AuctionTable.price],
            expired = resultRow[AuctionTable.expired]
        )
    }

    override suspend fun insertSlot(marketSlot: MarketSlot): Result<Int> {
        return query("insertSlot") {
            AuctionTable.insertAndGetId { statement ->
                statement[AuctionTable.minecraftUuid] = marketSlot.minecraftUuid
                statement[AuctionTable.minecraftUsername] = marketSlot.minecraftUsername
                statement[AuctionTable.time] = marketSlot.time.toEpochMilliseconds()
                statement[AuctionTable.item] = marketSlot.item.value
                statement[AuctionTable.price] = marketSlot.price
                statement[AuctionTable.expired] = marketSlot.expired
            }.value
        }
    }

    override suspend fun expireSlot(marketSlot: MarketSlot): Result<Unit> {
        return query("expireSlot") {
            AuctionTable.update(
                where = { AuctionTable.id.eq(marketSlot.id) },
                body = { statement -> statement[AuctionTable.expired] = true }
            )
            Unit
        }
    }

    override suspend fun loadUserSlots(
        uuid: String,
        isExpired: Boolean
    ): Result<List<MarketSlot>> {
        return query("loadUserSlots") {
            AuctionTable
                .selectAll()
                .where {
                    AuctionTable.minecraftUuid
                        .eq(uuid)
                        .and(AuctionTable.expired.eq(isExpired))
                }
                .map(::toMarketSlot)
        }
    }

    override suspend fun loadSlots(isExpired: Boolean): Result<List<MarketSlot>> {
        return query("loadSlots") {
            AuctionTable.selectAll()
                .where { AuctionTable.expired.eq(isExpired) }
                .map(::toMarketSlot)
        }
    }

    override suspend fun loadSlotsCreatedBefore(
        moment: Instant
    ): Result<List<MarketSlot>> {
        return query("loadSlotsCreatedBefore") {
            AuctionTable.selectAll()
                .where { AuctionTable.time.less(moment.toEpochMilliseconds()) }
                .map(::toMarketSlot)
        }
    }

    override suspend fun findSlot(id: Int): Result<MarketSlot?> {
        return query("findSlot") {
            AuctionTable.selectAll()
                .where { AuctionTable.id.eq(id) }
                .map(::toMarketSlot)
                .firstOrNull()
        }
    }

    override suspend fun deleteSlot(marketSlot: MarketSlot): Result<Unit> {
        return query("deleteSlot") {
            AuctionTable.deleteWhere { AuctionTable.id.eq(marketSlot.id) }
            Unit
        }
    }

    override suspend fun countPlayerSlots(uuid: String): Result<Int> {
        return query("countPlayerSlots") {
            AuctionTable.selectAll()
                .where { AuctionTable.minecraftUuid.eq(uuid) }
                .count()
                .toInt()
        }
    }
}
