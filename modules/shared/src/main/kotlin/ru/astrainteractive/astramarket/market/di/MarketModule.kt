package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.market.data.di.MarketDataModule
import ru.astrainteractive.astramarket.market.domain.di.MarketDomainModule
import ru.astrainteractive.astramarket.market.domain.di.PlatformMarketDomainModule
import ru.astrainteractive.astramarket.market.presentation.AuctionComponent
import ru.astrainteractive.astramarket.market.presentation.DefaultAuctionComponent
import ru.astrainteractive.astramarket.market.presentation.di.AuctionComponentDependencies
import ru.astrainteractive.klibs.kdi.Factory
import java.util.UUID

interface MarketModule {
    val marketDomainModule: MarketDomainModule
    fun createAuctionComponent(
        playerUUID: UUID,
        isExpired: Boolean,
        targetPlayerUUID: UUID?
    ): AuctionComponent

    class Default(
        private val coreModule: CoreModule,
        private val apiMarketModule: ApiMarketModule,
        marketDataModuleFactory: Factory<MarketDataModule>,
        platformMarketDomainModuleFactory: Factory<PlatformMarketDomainModule>
    ) : MarketModule {
        override val marketDomainModule: MarketDomainModule by lazy {
            MarketDomainModule.Default(
                coreModule = coreModule,
                apiMarketModule = apiMarketModule,
                marketDataModuleFactory = marketDataModuleFactory,
                platformMarketDomainModuleFactory = platformMarketDomainModuleFactory
            )
        }

        override fun createAuctionComponent(
            playerUUID: UUID,
            isExpired: Boolean,
            targetPlayerUUID: UUID?
        ): AuctionComponent {
            val dependencies = AuctionComponentDependencies.Default(
                coreModule = coreModule,
                apiMarketModule = apiMarketModule,
                marketDomainModule = marketDomainModule,
            )
            return DefaultAuctionComponent(
                playerUUID = playerUUID,
                targetPlayerUUID = targetPlayerUUID,
                isExpired = isExpired,
                dependencies = dependencies
            )
        }
    }
}
