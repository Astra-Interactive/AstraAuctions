package ru.astrainteractive.astramarket.gui.domain.data

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import java.util.UUID

interface AuctionsRepository {

    suspend fun getAuctionOrNull(id: Int): AuctionDTO?

    suspend fun isInventoryFull(uuid: UUID): Boolean

    suspend fun deleteAuction(auctionDTO: AuctionDTO): Unit?

    suspend fun addItemToInventory(auctionDTO: AuctionDTO, uuid: UUID)

    suspend fun itemDesc(auctionDTO: AuctionDTO): String
    fun playerName(uuid: UUID): String?
}
