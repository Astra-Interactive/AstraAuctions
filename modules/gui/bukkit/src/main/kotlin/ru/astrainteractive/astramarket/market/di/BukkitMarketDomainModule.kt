package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.market.api.AuctionsSorter
import ru.astrainteractive.astramarket.market.bukkit.BukkitAuctionsSorter

class BukkitMarketDomainModule(objectEncoder: ObjectEncoder) : PlatformMarketDomainModule {
    override val auctionsSorter: AuctionsSorter by lazy {
        BukkitAuctionsSorter(objectEncoder = objectEncoder)
    }
}
