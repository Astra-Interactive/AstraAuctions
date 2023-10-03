package ru.astrainteractive.astramarket.domain.di

import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.di.DataModule
import ru.astrainteractive.astramarket.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.domain.data.BukkitAuctionsRepository
import ru.astrainteractive.astramarket.domain.data.BukkitPlayerInteraction
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.domain.util.AuctionSorter
import ru.astrainteractive.astramarket.domain.util.BukkitAuctionSorter
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface BukkitDomainModule {
    val auctionsRepository: AuctionsRepository
    val playerInteraction: PlayerInteraction
    val auctionSorter: AuctionSorter

    class Default(
        private val dataModule: DataModule,
        private val encoder: Encoder,
        stringSerializer: KyoriComponentSerializer,
        permissionManager: PermissionManager
    ) : BukkitDomainModule {
        override val auctionsRepository: AuctionsRepository by Provider {
            BukkitAuctionsRepository(
                auctionsApi = dataModule.auctionApi,
                serializer = encoder,
                permissionManager = permissionManager
            )
        }
        override val playerInteraction: PlayerInteraction by Provider {
            BukkitPlayerInteraction(
                stringSerializer = stringSerializer
            )
        }
        override val auctionSorter: AuctionSorter by Provider {
            BukkitAuctionSorter(
                encoder = encoder
            )
        }
    }
}
