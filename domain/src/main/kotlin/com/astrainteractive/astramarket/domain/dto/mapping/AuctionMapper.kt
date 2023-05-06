package com.astrainteractive.astramarket.domain.dto.mapping

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.domain.entities.Auction
import ru.astrainteractive.astralibs.domain.mapping.Mapper
import ru.astrainteractive.astralibs.encoding.IO

object AuctionMapper : Mapper<Auction, AuctionDTO> {

    override fun toDTO(it: Auction): AuctionDTO = AuctionDTO(
        id = it.id,
        discordId = it.discordId,
        minecraftUuid = it.minecraftUuid,
        time = it.time,
        item = IO.ByteArray(it.item),
        price = it.price,
        expired = it.expired == 1
    )

    override fun fromDTO(it: AuctionDTO): Auction {
        error("Not implemented")
    }
}
