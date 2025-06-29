package ru.astrainteractive.astramarket.market.presentation.di

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AuctionComponentDependencies {
    val config: PluginConfig
    val dispatchers: KotlinDispatchers
    val marketApi: MarketApi
    val auctionBuyUseCase: AuctionBuyUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase
    val playerInteractionBridge: PlayerInteractionBridge
    val sortAuctionsUseCase: SortAuctionsUseCase

    class Default(
        coreModule: CoreModule,
        apiMarketModule: ApiMarketModule,
        marketViewDomainModule: MarketViewDomainModule,
    ) : AuctionComponentDependencies {
        override val config: PluginConfig by coreModule.configKrate
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val marketApi: MarketApi = apiMarketModule.marketApi
        override val auctionBuyUseCase: AuctionBuyUseCase = marketViewDomainModule.auctionBuyUseCase
        override val expireAuctionUseCase: ExpireAuctionUseCase = marketViewDomainModule.expireAuctionUseCase
        override val removeAuctionUseCase: RemoveAuctionUseCase = marketViewDomainModule.removeAuctionUseCase
        override val playerInteractionBridge = marketViewDomainModule.marketDataModule.playerInteractionBridge
        override val sortAuctionsUseCase = marketViewDomainModule.platformMarketDomainModule.sortAuctionsUseCase
    }
}
