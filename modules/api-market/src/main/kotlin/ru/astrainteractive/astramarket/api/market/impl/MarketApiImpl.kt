package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.db.market.entity.Auction
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.coroutines.CoroutineContext

internal class MarketApiImpl(
    private val database: Database,
    private val auctionMapper: AuctionMapper,
    dispatchers: KotlinDispatchers
) : MarketApi {
    private val limitedIoDispatcher = dispatchers.IO.limitedParallelism(1)
    private suspend fun <T> runCatchingWithContext(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
    ): Result<T> = runCatching {
        withContext(context, block)
    }

    override suspend fun insertSlot(marketSlot: MarketSlot): Int? = runCatchingWithContext(
        limitedIoDispatcher
    ) {
        AuctionTable.insert(database) {
            this[AuctionTable.discordId] = marketSlot.discordId
            this[AuctionTable.minecraftUuid] = marketSlot.minecraftUuid
            this[AuctionTable.time] = marketSlot.time
            this[AuctionTable.item] = marketSlot.item.value
            this[AuctionTable.price] = marketSlot.price
            this[AuctionTable.expired] = if (marketSlot.expired) 1 else 0
        }
    }.getOrNull()

    override suspend fun expireSlot(marketSlot: MarketSlot) = runCatchingWithContext(
        limitedIoDispatcher
    ) {
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
        expired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.minecraftUuid.eq(uuid).and(
                AuctionTable.expired.eq(if (expired) 1 else 0)
            )
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlots(
        expired: Boolean
    ): List<MarketSlot>? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.expired.eq(if (expired) 1 else 0)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlotsOlderThan(
        millis: Long
    ): List<MarketSlot>? = runCatchingWithContext(limitedIoDispatcher) {
        val currentTime = System.currentTimeMillis()
        val time = currentTime - millis
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.time.less(time)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getSlot(
        id: Int
    ): MarketSlot? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.id.eq(id)
        }.map(auctionMapper::toDTO).firstOrNull()
    }.getOrNull()

    override suspend fun deleteSlot(
        marketSlot: MarketSlot
    ) = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.delete<Auction>(database) {
            AuctionTable.id.eq(marketSlot.id)
        }
    }.getOrNull()

    override suspend fun countPlayerSlots(
        uuid: String
    ): Int? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.count(database) {
            AuctionTable.minecraftUuid.eq(uuid)
        }
    }.getOrNull()
}
