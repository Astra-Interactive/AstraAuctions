package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.db.market.entity.Auction
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.coroutines.CoroutineContext

internal class AuctionsAPIImpl(
    private val database: Database,
    private val auctionMapper: AuctionMapper,
    dispatchers: KotlinDispatchers
) : AuctionsAPI {
    private val limitedIoDispatcher = dispatchers.IO.limitedParallelism(1)
    private suspend fun <T> runCatchingWithContext(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
    ): Result<T> = runCatching {
        withContext(context, block)
    }

    override suspend fun insertAuction(auctionDTO: AuctionDTO): Int? = runCatchingWithContext(
        limitedIoDispatcher
    ) {
        AuctionTable.insert(database) {
            this[AuctionTable.discordId] = auctionDTO.discordId
            this[AuctionTable.minecraftUuid] = auctionDTO.minecraftUuid
            this[AuctionTable.time] = auctionDTO.time
            this[AuctionTable.item] = auctionDTO.item.value
            this[AuctionTable.price] = auctionDTO.price
            this[AuctionTable.expired] = if (auctionDTO.expired) 1 else 0
        }
    }.getOrNull()

    override suspend fun expireAuction(auctionDTO: AuctionDTO) = runCatchingWithContext(
        limitedIoDispatcher
    ) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.id.eq(auctionDTO.id)
        }.firstOrNull()?.let {
            it.expired = 1
            AuctionTable.update(database, entity = it)
        }
        Unit
    }.getOrNull()

    override suspend fun getUserAuctions(
        uuid: String,
        expired: Boolean
    ): List<AuctionDTO>? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.minecraftUuid.eq(uuid).and(
                AuctionTable.expired.eq(if (expired) 1 else 0)
            )
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getAuctions(
        expired: Boolean
    ): List<AuctionDTO>? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.expired.eq(if (expired) 1 else 0)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getAuctionsOlderThan(
        millis: Long
    ): List<AuctionDTO>? = runCatchingWithContext(limitedIoDispatcher) {
        val currentTime = System.currentTimeMillis()
        val time = currentTime - millis
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.time.less(time)
        }.map(auctionMapper::toDTO)
    }.getOrNull()

    override suspend fun fetchAuction(
        id: Int
    ): AuctionDTO? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.find(database, constructor = Auction) {
            AuctionTable.id.eq(id)
        }.map(auctionMapper::toDTO).firstOrNull()
    }.getOrNull()

    override suspend fun deleteAuction(
        auction: AuctionDTO
    ) = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.delete<Auction>(database) {
            AuctionTable.id.eq(auction.id)
        }
    }.getOrNull()

    override suspend fun countPlayerAuctions(
        uuid: String
    ): Int? = runCatchingWithContext(limitedIoDispatcher) {
        AuctionTable.count(database) {
            AuctionTable.minecraftUuid.eq(uuid)
        }
    }.getOrNull()
}
