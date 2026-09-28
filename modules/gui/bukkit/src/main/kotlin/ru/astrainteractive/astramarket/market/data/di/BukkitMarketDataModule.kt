package ru.astrainteractive.astramarket.market.data.di

import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitPlayerInteractionBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge

class BukkitMarketDataModule(
    itemStackEncoder: ItemStackEncoder
) : MarketDataModule {
    override val auctionBridge: AuctionsBridge by lazy {
        BukkitAuctionsBridge(
            itemStackEncoder = itemStackEncoder,
        )
    }
    override val playerInteractionBridge: PlayerInteractionBridge by lazy {
        BukkitPlayerInteractionBridge()
    }
}
