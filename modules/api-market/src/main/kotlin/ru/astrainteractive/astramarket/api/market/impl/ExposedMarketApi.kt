package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.CoroutineScope
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
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class ExposedMarketApi(
    private val databaseFlow: Flow<Database>,
    private val dispatchers: KotlinDispatchers
) : MarketApi,
    Logger by JUtiltLogger("AstraMarket-ExposedMarketApi").withoutParentHandlers() {
    private val mutex = Mutex()

    private suspend fun <T> runSafely(
        tag: String,
        block: suspend CoroutineScope.() -> T
    ): Result<T> = runCatching {
        mutex.withLock {
            withContext(dispatchers.IO, block)
        }
    }.onFailure { throwable -> error(throwable) { "$tag: Error during execution" } }

    private fun toMarketSlot(resultRow: ResultRow): MarketSlot {
        return MarketSlot(
            id = resultRow[AuctionTable.id].value,
            time = resultRow[AuctionTable.time],
            minecraftUuid = resultRow[AuctionTable.minecraftUuid],
            minecraftUsername = resultRow[AuctionTable.minecraftUsername],
            item = EncodedObject.ByteArray(resultRow[AuctionTable.item]),
            price = resultRow[AuctionTable.price],
            expired = resultRow[AuctionTable.expired]
        )
    }

    override suspend fun insertSlot(
        marketSlot: MarketSlot
    ) = runSafely("insertSlot") {
        transaction(databaseFlow.first()) {
            AuctionTable.insertAndGetId {
                it[AuctionTable.minecraftUuid] = marketSlot.minecraftUuid
                it[AuctionTable.minecraftUsername] = marketSlot.minecraftUsername
                it[AuctionTable.time] = marketSlot.time
                it[AuctionTable.item] = marketSlot.item.value
                it[AuctionTable.price] = marketSlot.price
                it[AuctionTable.expired] = marketSlot.expired
            }.value
        }
    }.getOrNull()

    override suspend fun expireSlot(
        marketSlot: MarketSlot
    ): Unit? = runSafely("expireSlot") {
        transaction(databaseFlow.first()) {
            AuctionTable.update(
                where = { AuctionTable.id.eq(marketSlot.id) },
                body = {
                    it[AuctionTable.expired] = true
                }
            )
        }
        Unit
    }.getOrNull()

    override suspend fun getUserSlots(
        uuid: String,
        isExpired: Boolean
    ): List<MarketSlot>? = runSafely("getUserSlots") {
        transaction(databaseFlow.first()) {
            AuctionTable
                .selectAll()
                .where {
                    AuctionTable.minecraftUuid
                        .eq(uuid)
                        .and(AuctionTable.expired.eq(isExpired))
                }.map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlots(
        isExpired: Boolean
    ): List<MarketSlot>? = runSafely("getSlots") {
        transaction(databaseFlow.first()) {
            AuctionTable.selectAll()
                .where { AuctionTable.expired.eq(isExpired) }
                .map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlotsOlderThan(
        millis: Long
    ): List<MarketSlot>? = runSafely("getSlotsOlderThan") {
        transaction(databaseFlow.first()) {
            val currentTime = System.currentTimeMillis()
            val time = currentTime - millis
            AuctionTable.selectAll()
                .where { AuctionTable.time.less(time) }
                .map(::toMarketSlot)
        }
    }.getOrNull()

    override suspend fun getSlot(
        id: Int
    ): MarketSlot? = runSafely("getSlot") {
        transaction(databaseFlow.first()) {
            AuctionTable.selectAll()
                .where { AuctionTable.id.eq(id) }
                .map(::toMarketSlot)
                .firstOrNull()
        }
    }.getOrNull()

    override suspend fun deleteSlot(
        marketSlot: MarketSlot
    ): Unit? = runSafely("deleteSlot") {
        transaction(databaseFlow.first()) {
            AuctionTable.deleteWhere { AuctionTable.id.eq(marketSlot.id) }
        }
        Unit
    }.getOrNull()

    override suspend fun countPlayerSlots(
        uuid: String
    ): Int? = runSafely("countPlayerSlots") {
        transaction(databaseFlow.first()) {
            AuctionTable.selectAll()
                .where { AuctionTable.minecraftUuid.eq(uuid) }
                .count()
                .toInt()
        }
    }.getOrNull()
}
