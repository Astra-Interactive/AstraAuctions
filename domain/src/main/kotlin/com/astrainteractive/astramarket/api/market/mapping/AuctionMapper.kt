package com.astrainteractive.astramarket.api.market.mapping

import com.astrainteractive.astramarket.api.market.dto.AuctionDTO
import com.astrainteractive.astramarket.db.market.entity.Auction
import ru.astrainteractive.astralibs.encoding.IO
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

interface AuctionMapper : Mapper<Auction, AuctionDTO>

internal class AuctionMapperImpl : AuctionMapper {

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
