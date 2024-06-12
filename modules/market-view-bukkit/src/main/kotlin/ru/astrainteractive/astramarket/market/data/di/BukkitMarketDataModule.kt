package ru.astrainteractive.astramarket.market.data.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitPlayerInteractionBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitMarketDataModule(
    itemStackEncoder: ItemStackEncoder,
    stringSerializer: KyoriComponentSerializer,
) : MarketDataModule {
    override val auctionBridge: AuctionsBridge by Provider {
        BukkitAuctionsBridge(
            itemStackEncoder = itemStackEncoder,
        )
    }
    override val playerInteractionBridge: PlayerInteractionBridge by Provider {
        BukkitPlayerInteractionBridge(
            stringSerializer = stringSerializer
        )
    }
}
