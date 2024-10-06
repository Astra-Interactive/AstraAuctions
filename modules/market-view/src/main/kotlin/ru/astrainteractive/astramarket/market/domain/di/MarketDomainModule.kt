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
        override val marketDataModule: MarketDataModule,
        override val platformMarketDomainModule: PlatformMarketDomainModule
    ) : MarketDomainModule {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by lazy {
            AuctionSortTranslationMappingImpl(
                translation = coreModule.translation.cachedValue
            )
        }
        override val auctionBuyUseCase: AuctionBuyUseCase by lazy {
            AuctionBuyUseCaseImpl(
                translation = coreModule.translation.cachedValue,
                config = coreModule.config.cachedValue,
                economyProviderFactory = coreModule.economyProviderFactory,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by lazy {
            CreateAuctionUseCaseImpl(
                translation = coreModule.translation.cachedValue,
                config = coreModule.config.cachedValue,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by lazy {
            ExpireAuctionUseCaseImpl(
                translation = coreModule.translation.cachedValue,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by lazy {
            RemoveAuctionUseCaseImpl(
                translation = coreModule.translation.cachedValue,
                config = coreModule.config.cachedValue,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketApi = apiMarketModule.marketApi
            )
        }
    }
}
