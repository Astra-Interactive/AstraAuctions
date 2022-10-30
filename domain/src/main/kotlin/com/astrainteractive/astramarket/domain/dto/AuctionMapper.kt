package com.astrainteractive.astramarket.domain.dto

import com.astrainteractive.astramarket.domain.entities.Auction
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import ru.astrainteractive.astralibs.domain.mapping.IMapper

object AuctionMapper : IMapper<Auction, AuctionDTO> {

    override fun toDTO(it: Auction): AuctionDTO = AuctionDTO(
        id = it.id,
        discordId = it.discordId,
        minecraftUuid = it.minecraftUuid,
        time = it.time,
        item = it.item,
        price = it.price,
        expired = it.expired==1
    )

    override fun fromDTO(it: AuctionDTO): Auction {
        return AuctionTable.find(constructor = ::Auction) {
            AuctionTable.id.eq(it.id)
        }.first()
    }
}