package ru.astrainteractive.astramarket.market.domain.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.market.data.di.MarketDataModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMappingImpl
import ru.astrainteractive.astramarket.market.usecase.BuyAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.usecase.RemoveAuctionUseCase

interface MarketViewDomainModule {
    val marketDataModule: MarketDataModule
    val platformMarketDomainModule: PlatformMarketDomainModule

    // Mappers
    val auctionSortTranslationMapping: AuctionSortTranslationMapping

    // UseCases
    val buyAuctionUseCase: BuyAuctionUseCase
    val createAuctionUseCase: CreateAuctionUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase

    class Default(
        coreModule: CoreModule,
        marketRepository: MarketRepository,
        override val marketDataModule: MarketDataModule,
        override val platformMarketDomainModule: PlatformMarketDomainModule
    ) : MarketViewDomainModule {

        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by lazy {
            AuctionSortTranslationMappingImpl(
                pluginTranslationKrate = coreModule.pluginTranslationKrate
            )
        }
        override val buyAuctionUseCase: BuyAuctionUseCase by lazy {
            BuyAuctionUseCase(
                pluginTranslationKrate = coreModule.pluginTranslationKrate,
                configKrate = coreModule.configKrate,
                economyProviderFactory = coreModule.economyProviderFactory,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketRepository = marketRepository
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by lazy {
            CreateAuctionUseCase(
                pluginTranslationKrate = coreModule.pluginTranslationKrate,
                configKrate = coreModule.configKrate,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketRepository = marketRepository
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by lazy {
            ExpireAuctionUseCase(
                pluginTranslationKrate = coreModule.pluginTranslationKrate,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketRepository = marketRepository
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by lazy {
            RemoveAuctionUseCase(
                pluginTranslationKrate = coreModule.pluginTranslationKrate,
                configKrate = coreModule.configKrate,
                auctionsBridge = marketDataModule.auctionBridge,
                playerInteractionBridge = marketDataModule.playerInteractionBridge,
                marketRepository = marketRepository
            )
        }
    }
}
