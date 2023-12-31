package ru.astrainteractive.astramarket.domain.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.di.SharedDataModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMappingImpl
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCaseImpl
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCaseImpl
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCaseImpl
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCaseImpl
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface SharedDomainModule {
    val sharedDataModule: SharedDataModule
    val platformSharedDomainModule: PlatformSharedDomainModule

    // Mappers
    val auctionSortTranslationMapping: AuctionSortTranslationMapping

    // UseCases
    val auctionBuyUseCase: AuctionBuyUseCase
    val createAuctionUseCase: CreateAuctionUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase

    class Default(
        coreModule: CoreModule,
        apiMarketModule: ApiMarketModule,
        sharedDataModuleFactory: Factory<SharedDataModule>,
        platformSharedDomainModuleFactory: Factory<PlatformSharedDomainModule>
    ) : SharedDomainModule {
        override val sharedDataModule: SharedDataModule by Provider {
            sharedDataModuleFactory.create()
        }
        override val platformSharedDomainModule: PlatformSharedDomainModule by Provider {
            platformSharedDomainModuleFactory.create()
        }
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by Provider {
            AuctionSortTranslationMappingImpl(
                translation = coreModule.translation.value
            )
        }
        override val auctionBuyUseCase: AuctionBuyUseCase by Provider {
            AuctionBuyUseCaseImpl(
                translation = coreModule.translation.value,
                config = coreModule.config.value,
                economyProvider = coreModule.economyProvider,
                auctionsBridge = sharedDataModule.auctionBridge,
                playerInteractionBridge = sharedDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            CreateAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                config = coreModule.config.value,
                auctionsBridge = sharedDataModule.auctionBridge,
                playerInteractionBridge = sharedDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by Provider {
            ExpireAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                auctionsBridge = sharedDataModule.auctionBridge,
                playerInteractionBridge = sharedDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by Provider {
            RemoveAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                config = coreModule.config.value,
                auctionsBridge = sharedDataModule.auctionBridge,
                playerInteractionBridge = sharedDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
    }
}
