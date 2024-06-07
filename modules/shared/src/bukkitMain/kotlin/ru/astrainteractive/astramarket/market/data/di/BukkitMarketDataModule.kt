package ru.astrainteractive.astramarket.market.data.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.BukkitPlayerInteractionBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitMarketDataModule(
    encoder: ObjectEncoder,
    stringSerializer: KyoriComponentSerializer,
) : MarketDataModule {
    override val auctionBridge: AuctionsBridge by Provider {
        BukkitAuctionsBridge(
            encoder = encoder,
        )
    }
    override val playerInteractionBridge: PlayerInteractionBridge by Provider {
        BukkitPlayerInteractionBridge(
            stringSerializer = stringSerializer
        )
    }
}
