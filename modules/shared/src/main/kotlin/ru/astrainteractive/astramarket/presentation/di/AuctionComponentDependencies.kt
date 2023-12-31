package ru.astrainteractive.astramarket.presentation.di

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.astramarket.data.di.SharedDataModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.klibs.kdi.getValue
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
        sharedDomainModule: SharedDomainModule,
        sharedDataModule: SharedDataModule
    ) : AuctionComponentDependencies {
        override val config: PluginConfig by coreModule.config
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val marketApi: MarketApi = apiMarketModule.marketApi
        override val auctionBuyUseCase: AuctionBuyUseCase = sharedDomainModule.auctionBuyUseCase
        override val expireAuctionUseCase: ExpireAuctionUseCase = sharedDomainModule.expireAuctionUseCase
        override val removeAuctionUseCase: RemoveAuctionUseCase = sharedDomainModule.removeAuctionUseCase
        override val playerInteractionBridge: PlayerInteractionBridge = sharedDataModule.playerInteractionBridge
        override val sortAuctionsUseCase = sharedDomainModule.platformSharedDomainModule.sortAuctionsUseCase
    }
}
