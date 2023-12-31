package ru.astrainteractive.astramarket.data.di

import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.data.bridge.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.data.bridge.BukkitPlayerInteractionBridge
import ru.astrainteractive.astramarket.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitSharedDataModule(
    encoder: Encoder,
    stringSerializer: KyoriComponentSerializer,
) : SharedDataModule {
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
