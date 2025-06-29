package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.market.data.di.MarketDataModule
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.di.PlatformMarketDomainModule
import ru.astrainteractive.astramarket.market.presentation.AuctionComponent
import ru.astrainteractive.astramarket.market.presentation.DefaultAuctionComponent
import ru.astrainteractive.astramarket.market.presentation.di.AuctionComponentDependencies
import java.util.UUID

interface MarketViewModule {
    val marketViewDomainModule: MarketViewDomainModule

    fun createAuctionComponent(
        playerUUID: UUID,
        isExpired: Boolean,
        targetPlayerUUID: UUID?
    ): AuctionComponent

    class Default(
        private val coreModule: CoreModule,
        private val apiMarketModule: ApiMarketModule,
        marketDataModule: MarketDataModule,
        platformMarketDomainModule: PlatformMarketDomainModule
    ) : MarketViewModule {
        override val marketViewDomainModule: MarketViewDomainModule by lazy {
            MarketViewDomainModule.Default(
                coreModule = coreModule,
                apiMarketModule = apiMarketModule,
                marketDataModule = marketDataModule,
                platformMarketDomainModule = platformMarketDomainModule
            )
        }

        override fun createAuctionComponent(
            playerUUID: UUID,
            isExpired: Boolean,
            targetPlayerUUID: UUID?
        ): AuctionComponent {
            return DefaultAuctionComponent(
                playerUUID = playerUUID,
                targetPlayerUUID = targetPlayerUUID,
                isExpired = isExpired,
                dependencies = AuctionComponentDependencies.Default(
                    coreModule = coreModule,
                    apiMarketModule = apiMarketModule,
                    marketViewDomainModule = marketViewDomainModule,
                )
            )
        }
    }
}
