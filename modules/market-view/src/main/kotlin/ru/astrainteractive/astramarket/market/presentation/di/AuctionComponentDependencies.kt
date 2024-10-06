package ru.astrainteractive.astramarket.market.presentation.di

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.domain.di.MarketDomainModule
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
        marketDomainModule: MarketDomainModule,
    ) : AuctionComponentDependencies {
        override val config: PluginConfig by coreModule.config
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val marketApi: MarketApi = apiMarketModule.marketApi
        override val auctionBuyUseCase: AuctionBuyUseCase = marketDomainModule.auctionBuyUseCase
        override val expireAuctionUseCase: ExpireAuctionUseCase = marketDomainModule.expireAuctionUseCase
        override val removeAuctionUseCase: RemoveAuctionUseCase = marketDomainModule.removeAuctionUseCase
        override val playerInteractionBridge = marketDomainModule.marketDataModule.playerInteractionBridge
        override val sortAuctionsUseCase = marketDomainModule.platformMarketDomainModule.sortAuctionsUseCase
    }
}
