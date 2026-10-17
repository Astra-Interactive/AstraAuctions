package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.market.component.AuctionComponent
import ru.astrainteractive.astramarket.market.component.DefaultAuctionComponent
import ru.astrainteractive.klibs.kstorage.api.getValue
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
        private val marketRepository: MarketRepository,
        marketDataModule: MarketDataModule,
        platformMarketDomainModule: PlatformMarketDomainModule
    ) : MarketViewModule {
        override val marketViewDomainModule: MarketViewDomainModule by lazy {
            MarketViewDomainModule.Default(
                coreModule = coreModule,
                marketRepository = marketRepository,
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
                configKrate = coreModule.configKrate,
                marketRepository = marketRepository,
                auctionsSorter = marketViewDomainModule.platformMarketDomainModule.auctionsSorter,
                removeAuctionUseCase = marketViewDomainModule.removeAuctionUseCase,
                buyAuctionUseCase = marketViewDomainModule.buyAuctionUseCase,
                expireAuctionUseCase = marketViewDomainModule.expireAuctionUseCase,
                playerInteractionBridge = marketViewDomainModule.marketDataModule.playerInteractionBridge
            )
        }
    }
}
