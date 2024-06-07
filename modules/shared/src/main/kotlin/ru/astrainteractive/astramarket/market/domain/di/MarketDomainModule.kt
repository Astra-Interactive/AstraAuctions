package ru.astrainteractive.astramarket.market.domain.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.market.data.di.MarketDataModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMappingImpl
import ru.astrainteractive.astramarket.market.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.AuctionBuyUseCaseImpl
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCaseImpl
import ru.astrainteractive.astramarket.market.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.ExpireAuctionUseCaseImpl
import ru.astrainteractive.astramarket.market.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.RemoveAuctionUseCaseImpl
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface MarketDomainModule {
    val marketDataModule: MarketDataModule
    val platformMarketDomainModule: PlatformMarketDomainModule

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
        marketDataModuleFactory: Factory<MarketDataModule>,
        platformMarketDomainModuleFactory: Factory<PlatformMarketDomainModule>
    ) : MarketDomainModule {
        override val marketDataModule: MarketDataModule by Provider {
            marketDataModuleFactory.create()
        }
        override val platformMarketDomainModule: PlatformMarketDomainModule by Provider {
            platformMarketDomainModuleFactory.create()
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
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            CreateAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                config = coreModule.config.value,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by Provider {
            ExpireAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by Provider {
            RemoveAuctionUseCaseImpl(
                translation = coreModule.translation.value,
                config = coreModule.config.value,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
    }
}
