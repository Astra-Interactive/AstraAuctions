package ru.astrainteractive.astramarket.gui.domain.di

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.gui.domain.data.impl.BukkitAuctionsRepository
import ru.astrainteractive.astramarket.gui.domain.data.impl.BukkitPlayerInteraction
import ru.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMappingImpl
import ru.astrainteractive.astramarket.gui.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.AuctionBuyUseCaseImpl
import ru.astrainteractive.astramarket.gui.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.CreateAuctionUseCaseImpl
import ru.astrainteractive.astramarket.gui.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.ExpireAuctionUseCaseImpl
import ru.astrainteractive.astramarket.gui.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.gui.domain.usecase.RemoveAuctionUseCaseImpl
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface GuiDomainModule {
    // Mappers
    val auctionSortTranslationMapping: AuctionSortTranslationMapping

    // UseCases
    val auctionBuyUseCase: AuctionBuyUseCase
    val createAuctionUseCase: CreateAuctionUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase

    class Default(
        auctionsAPI: AuctionsAPI,
        translation: Translation,
        configuration: AuctionConfig,
        economyProvider: EconomyProvider,
        serializer: Encoder,
        stringSerializer: KyoriComponentSerializer,
        permissionManager: PermissionManager
    ) : GuiDomainModule {
        private val auctionsRepository by Provider {
            BukkitAuctionsRepository(
                dataSource = auctionsAPI,
                serializer = serializer,
                permissionManager = permissionManager
            )
        }
        private val playerInteraction by Provider {
            BukkitPlayerInteraction(
                stringSerializer = stringSerializer
            )
        }
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by Provider {
            AuctionSortTranslationMappingImpl(
                translation = translation
            )
        }
        override val auctionBuyUseCase: AuctionBuyUseCase by Provider {
            AuctionBuyUseCaseImpl(
                translation = translation,
                config = configuration,
                economyProvider = economyProvider,
                auctionsRepository = auctionsRepository,
                playerInteraction = playerInteraction
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            CreateAuctionUseCaseImpl(
                translation = translation,
                config = configuration,
                auctionsRepository = auctionsRepository,
                playerInteraction = playerInteraction
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by Provider {
            ExpireAuctionUseCaseImpl(
                translation = translation,
                auctionsRepository = auctionsRepository,
                playerInteraction = playerInteraction
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by Provider {
            RemoveAuctionUseCaseImpl(
                translation = translation,
                config = configuration,
                auctionsRepository = auctionsRepository,
                playerInteraction = playerInteraction
            )
        }
    }
}
