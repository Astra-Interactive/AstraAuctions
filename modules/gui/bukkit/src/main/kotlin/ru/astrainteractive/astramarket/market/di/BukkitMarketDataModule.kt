package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.bukkit.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.market.bukkit.BukkitPlayerInteractionBridge

class BukkitMarketDataModule(
    objectEncoder: ObjectEncoder
) : MarketDataModule {
    override val auctionBridge: AuctionsBridge by lazy {
        BukkitAuctionsBridge(
            objectEncoder = objectEncoder,
        )
    }
    override val playerInteractionBridge: PlayerInteractionBridge by lazy {
        BukkitPlayerInteractionBridge()
    }
}
