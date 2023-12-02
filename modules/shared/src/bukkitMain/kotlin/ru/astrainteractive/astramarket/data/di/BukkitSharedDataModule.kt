package ru.astrainteractive.astramarket.data.di

import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.data.AuctionsBridge
import ru.astrainteractive.astramarket.data.BukkitAuctionsBridge
import ru.astrainteractive.astramarket.data.BukkitPlayerInteractionBridge
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge
import ru.astrainteractive.astramarket.di.DataModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitSharedDataModule(
    dataModule: DataModule,
    encoder: Encoder,
    stringSerializer: KyoriComponentSerializer,
) : SharedDataModule {
    override val auctionBridge: AuctionsBridge by Provider {
        BukkitAuctionsBridge(
            auctionsApi = dataModule.auctionApi,
            serializer = encoder,
        )
    }
    override val playerInteractionBridge: PlayerInteractionBridge by Provider {
        BukkitPlayerInteractionBridge(
            stringSerializer = stringSerializer
        )
    }
}
