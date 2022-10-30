package com.astrainteractive.astramarket.domain

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.domain.dto.AuctionMapper
import com.astrainteractive.astramarket.domain.entities.Auction
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import ru.astrainteractive.astralibs.database_v2.Database
import ru.astrainteractive.astralibs.utils.catching

interface IDataSource {
    suspend fun insertAuction(auctionDTO: AuctionDTO): Long?
    suspend fun expireAuction(auctionDTO: AuctionDTO): Unit?
    suspend fun getUserAuctions(uuid: String, expired: Boolean): List<AuctionDTO>?
    suspend fun getAuctions(expired: Boolean): List<AuctionDTO>?
    suspend fun getAuctionsOlderThan(millis: Long): List<AuctionDTO>?
    suspend fun fetchAuction(id: Long): AuctionDTO?
    suspend fun deleteAuction(auction: AuctionDTO): Unit?
    suspend fun countPlayerAuctions(uuid: String): Int?
}

class DataSource(private val database: Database) : IDataSource {
    override suspend fun insertAuction(auctionDTO: AuctionDTO): Long? = catching {
        return AuctionTable.insert {
            this[AuctionTable.discordId] = auctionDTO.discordId
            this[AuctionTable.minecraftUuid] = auctionDTO.minecraftUuid
            this[AuctionTable.time] = auctionDTO.time
            this[AuctionTable.item] = auctionDTO.item
            this[AuctionTable.price] = auctionDTO.price
            this[AuctionTable.expired] = if (auctionDTO.expired) 1 else 0
        }.toLong()
    }

    override suspend fun expireAuction(auctionDTO: AuctionDTO) = catching {
        AuctionTable.find(constructor = ::Auction) {
            AuctionTable.id.eq(auctionDTO.id)
        }?.firstOrNull()?.let {
            it.expired = 1
            AuctionTable.update(entity = it)
        }
        Unit
    }

    override suspend fun getUserAuctions(uuid: String, expired: Boolean): List<AuctionDTO>? = catching {
        return AuctionTable.find(constructor = ::Auction) {
            AuctionTable.minecraftUuid.eq(uuid).and(
                AuctionTable.expired.eq(if (expired) 1 else 0)
            )
        }.map(AuctionMapper::toDTO)
    }

    override suspend fun getAuctions(expired: Boolean): List<AuctionDTO>? = catching {
        return AuctionTable.find(constructor = ::Auction) {
            AuctionTable.expired.eq(if (expired) 1 else 0)
        }.map(AuctionMapper::toDTO)
    }

    override suspend fun getAuctionsOlderThan(millis: Long): List<AuctionDTO>? = catching {
        val currentTime = System.currentTimeMillis()
        val time = currentTime - millis
        return AuctionTable.find(constructor = ::Auction) {
            AuctionTable.time.less(time)
        }.map(AuctionMapper::toDTO)
    }

    override suspend fun fetchAuction(id: Long): AuctionDTO? = catching {
        return AuctionTable.find(constructor = ::Auction) {
            AuctionTable.id.eq(id)
        }.map(AuctionMapper::toDTO)?.firstOrNull()
    }

    override suspend fun deleteAuction(auction: AuctionDTO) = catching {
        AuctionTable.delete<Auction> {
            AuctionTable.id.eq(auction.id)
        }
    }

    override suspend fun countPlayerAuctions(uuid: String): Int? = catching {
        return AuctionTable.count {
            AuctionTable.minecraftUuid.eq(uuid)
        }
    }
}