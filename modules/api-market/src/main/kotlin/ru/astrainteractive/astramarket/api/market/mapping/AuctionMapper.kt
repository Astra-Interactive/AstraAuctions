package ru.astrainteractive.astramarket.api.market.mapping

import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.db.market.entity.Auction
import ru.astrainteractive.klibs.mikro.core.domain.Mapper

internal interface AuctionMapper : Mapper<Auction, MarketSlot>

internal class AuctionMapperImpl : AuctionMapper {

    override fun toDTO(it: Auction): MarketSlot =
        MarketSlot(
            id = it.id,
            minecraftUuid = it.minecraftUuid,
            time = it.time,
            item = EncodedObject.ByteArray(it.item),
            price = it.price,
            expired = it.expired == 1
        )

    override fun fromDTO(it: MarketSlot): Auction {
        error("Not implemented")
    }
}
