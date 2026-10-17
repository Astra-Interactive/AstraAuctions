package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.bukkit.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.market.bukkit.BukkitPlayerInteractionBridge

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
