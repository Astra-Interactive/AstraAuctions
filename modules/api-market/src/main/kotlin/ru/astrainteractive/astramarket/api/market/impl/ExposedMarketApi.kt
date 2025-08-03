package ru.astrainteractive.astramarket.api.market.impl

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.db.market.entity.AuctionTableV2
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class ExposedMarketApi(
    private val databaseFlow: Flow<Database>,
    private val dispatchers: KotlinDispatchers
) : MarketApi,
    Logger by JUtiltLogger("AstraMarket-ExposedMarketApi") {
    private val mutex = Mutex()

    private suspend fun <T> runCatchingWithContext(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
    ): Result<T> = runCatching {
        mutex.withLock { withContext(context, block) }
    }.onFailure { throwable -> error(throwable) { "Error during execution" } }

    private fun toMarketSlot(resultRow: ResultRow): MarketSlot {
        return MarketSlot(
            id = resultRow[AuctionTableV2.id].value,
            time = resultRow[AuctionTableV2.time],
            minecraftUuid = resultRow[AuctionTableV2.minecraftUuid],
            minecraftUsername = resultRow[AuctionTableV2.minecraftUsername],
            item = resultRow[AuctionTableV2.item],
            price = resultRow[AuctionTableV2.price],
            expired = resultRow[AuctionTableV2.expired]
        )
    }

    override suspend fun insertSlot(
        marketSlot: MarketSlot
    ) = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.insertAndGetId {
                it[AuctionTableV2.minecraftUuid] = marketSlot.minecraftUuid
                it[AuctionTableV2.minecraftUsername] = marketSlot.minecraftUsername
                it[AuctionTableV2.time] = marketSlot.time
                it[AuctionTableV2.item] = marketSlot.item
                it[AuctionTableV2.price] = marketSlot.price
                it[AuctionTableV2.expired] = marketSlot.expired
            }.value
        }
    }.getOrNull()

    override suspend fun expireSlot(
        marketSlot: MarketSlot
    ): Unit? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.update(
                where = { AuctionTableV2.id.eq(marketSlot.id) },
                body = {
                    it[AuctionTableV2.expired] = true
                }
            )
        }
        Unit
    }.getOrNull()

    override suspend fun getUserSlots(
        uuid: String,
        isExpired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2
                .selectAll()
                .where {
                    AuctionTableV2.minecraftUuid
                        .eq(uuid)
                        .and(AuctionTableV2.expired.eq(isExpired))
                }.map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlots(
        isExpired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.selectAll()
                .where { AuctionTableV2.expired.eq(isExpired) }
                .map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlotsOlderThan(
        millis: Long
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            val currentTime = System.currentTimeMillis()
            val time = currentTime - millis
            AuctionTableV2.selectAll()
                .where { AuctionTableV2.time.less(time) }
                .map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlot(
        id: Int
    ): MarketSlot? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.selectAll()
                .where { AuctionTableV2.id.eq(id) }
                .map(::toMarketSlot)
                .firstOrNull()
        }
    }.getOrNull()

    override suspend fun deleteSlot(
        marketSlot: MarketSlot
    ): Unit? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.deleteWhere { AuctionTableV2.id.eq(marketSlot.id) }
        }
        Unit
    }.getOrNull()

    override suspend fun countPlayerSlots(
        uuid: String
    ): Int? = runCatchingWithContext(dispatchers.IO) {
        transaction(databaseFlow.first()) {
            AuctionTableV2.selectAll()
                .where { AuctionTableV2.minecraftUuid.eq(uuid) }
                .count()
                .toInt()
        }
    }.getOrNull()
}
