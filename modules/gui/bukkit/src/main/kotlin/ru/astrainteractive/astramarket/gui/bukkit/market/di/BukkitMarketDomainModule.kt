package ru.astrainteractive.astramarket.gui.bukkit.market.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsSorter
import ru.astrainteractive.astramarket.gui.api.market.di.PlatformMarketDomainModule
import ru.astrainteractive.astramarket.gui.bukkit.market.bukkit.BukkitAuctionsSorter

class BukkitMarketDomainModule(objectEncoder: ObjectEncoder) : PlatformMarketDomainModule {
    override val auctionsSorter: AuctionsSorter by lazy {
        BukkitAuctionsSorter(objectEncoder = objectEncoder)
    }
}
