package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.api.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.db.market.entity.Auction
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID
import kotlin.coroutines.CoroutineContext

internal class ExposedMarketApi(
    private val database: Database,
    private val auctionMapper: AuctionMapper,
    private val dispatchers: KotlinDispatchers
) : MarketApi,
    Logger by JUtiltLogger("SqlMarketApi") {
    private val mutex = Mutex()

    private suspend fun <T> runCatchingWithContext(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
    ): Result<T> = runCatching {
        mutex.withLock { withContext(context, block) }
    }.onFailure { throwable -> error(throwable) { "Error during execution" } }

    override suspend fun insertSlot(
        marketSlot: MarketSlot
    ): Int? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.insert(database) {
            this[AuctionTable.minecraftUuid] = marketSlot.minecraftUuid
            this[AuctionTable.time] = marketSlot.time
            this[AuctionTable.item] = marketSlot.item.value
            this[AuctionTable.price] = marketSlot.price
            this[AuctionTable.expired] = if (marketSlot.expired) 1 else 0
        }
    }.getOrNull()

    override suspend fun expireSlot(
        marketSlot: MarketSlot
    ): Unit? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.id.eq(marketSlot.id)
        }.firstOrNull()?.let {
            it.expired = 1
            AuctionTable.update(database, entity = it)
        }
        Unit
    }.getOrNull()

    override suspend fun getUserSlots(
        uuid: String,
        isExpired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.minecraftUuid.eq(uuid).and(
                AuctionTable.expired.eq(if (isExpired) 1 else 0)
            )
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlots(
        isExpired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.expired.eq(if (isExpired) 1 else 0)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlotsOlderThan(
        millis: Long
    ): List<MarketSlot>? = runCatchingWithContext(dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val time = currentTime - millis
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.time.less(time)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlot(
        id: Int
    ): MarketSlot? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.id.eq(id)
        }.map(auctionMapper::toDTO).first()
    }.getOrNull()

    override suspend fun deleteSlot(
        marketSlot: MarketSlot
    ): Unit? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.delete<Auction>(database) {
            AuctionTable.id.eq(marketSlot.id)
        }
    }.getOrNull()

    override suspend fun countPlayerSlots(
        uuid: String
    ): Int? = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.count(database) {
            AuctionTable.minecraftUuid.eq(uuid)
        }
    }.getOrNull()

    override suspend fun findPlayersWithSlots(
        isExpired: Boolean
    ): List<PlayerAndSlots> = runCatchingWithContext(dispatchers.IO) {
        AuctionTable.all(database, Auction)
            .map(auctionMapper::toDTO)
            .groupBy(MarketSlot::minecraftUuid)
            .map { (uuid, slots) ->
                PlayerAndSlots(
                    minecraftUUID = UUID.fromString(uuid),
                    slots = slots
                )
            }
    }.getOrNull().orEmpty()
}
