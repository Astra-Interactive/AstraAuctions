package ru.astrainteractive.astramarket.gui.bukkit.market.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.gui.api.market.api.PlayerInteractionBridge
import ru.astrainteractive.astramarket.gui.api.market.di.MarketDataModule
import ru.astrainteractive.astramarket.gui.bukkit.market.bukkit.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.gui.bukkit.market.bukkit.BukkitPlayerInteractionBridge

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
