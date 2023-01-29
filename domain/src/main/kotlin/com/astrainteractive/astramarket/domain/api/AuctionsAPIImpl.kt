package com.astrainteractive.astramarket.domain.api

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.domain.dto.mapping.AuctionMapper
import com.astrainteractive.astramarket.domain.entities.Auction
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import ru.astrainteractive.astralibs.orm.Database

class AuctionsAPIImpl(private val database: Database) : AuctionsAPI {
    override suspend fun insertAuction(auctionDTO: AuctionDTO): Int? = kotlin.runCatching {
        return AuctionTable.insert(database) {
            this[AuctionTable.discordId] = auctionDTO.discordId
            this[AuctionTable.minecraftUuid] = auctionDTO.minecraftUuid
            this[AuctionTable.time] = auctionDTO.time
            this[AuctionTable.item] = auctionDTO.item.value
            this[AuctionTable.price] = auctionDTO.price
            this[AuctionTable.expired] = if (auctionDTO.expired) 1 else 0
        }
    }.getOrNull()

    override suspend fun expireAuction(auctionDTO: AuctionDTO) = kotlin.runCatching {
        AuctionTable.find(database,constructor = Auction) {
            AuctionTable.id.eq(auctionDTO.id)
        }?.firstOrNull()?.let {
            it.expired = 1
            AuctionTable.update(database,entity = it)
        }
        Unit
    }.getOrNull()

    override suspend fun getUserAuctions(uuid: String, expired: Boolean): List<AuctionDTO>? = kotlin.runCatching {
        return AuctionTable.find(database,constructor = Auction) {
            AuctionTable.minecraftUuid.eq(uuid).and(
                AuctionTable.expired.eq(if (expired) 1 else 0)
            )
        }.map(AuctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getAuctions(expired: Boolean): List<AuctionDTO>? = kotlin.runCatching {
        return AuctionTable.find(database,constructor = Auction) {
            AuctionTable.expired.eq(if (expired) 1 else 0)
        }.map(AuctionMapper::toDTO)
    }.getOrNull()

    override suspend fun getAuctionsOlderThan(millis: Long): List<AuctionDTO>? = kotlin.runCatching {
        val currentTime = System.currentTimeMillis()
        val time = currentTime - millis
        return AuctionTable.find(database,constructor = Auction) {
            AuctionTable.time.less(time)
        }.map(AuctionMapper::toDTO)
    }.getOrNull()

    override suspend fun fetchAuction(id: Int): AuctionDTO? = kotlin.runCatching {
        return AuctionTable.find(database,constructor = Auction) {
            AuctionTable.id.eq(id)
        }.map(AuctionMapper::toDTO)?.firstOrNull()
    }.getOrNull()

    override suspend fun deleteAuction(auction: AuctionDTO) = kotlin.runCatching {
        AuctionTable.delete<Auction>(database) {
            AuctionTable.id.eq(auction.id)
        }
    }.getOrNull()

    override suspend fun countPlayerAuctions(uuid: String): Int? = kotlin.runCatching {
        return AuctionTable.count(database) {
            AuctionTable.minecraftUuid.eq(uuid)
        }
    }.getOrNull()
}