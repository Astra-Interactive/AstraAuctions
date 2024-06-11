package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.button.AaucButtonFactory
import ru.astrainteractive.astramarket.gui.button.AuctionMarketItemButtonFactory
import ru.astrainteractive.astramarket.gui.button.AuctionSortButtonFactory
import ru.astrainteractive.astramarket.gui.button.BackButtonFactory
import ru.astrainteractive.astramarket.gui.button.BorderButtonFactory
import ru.astrainteractive.astramarket.gui.button.ExpiredButtonFactory
import ru.astrainteractive.astramarket.gui.button.ExpiredMarketItemButtonFactory
import ru.astrainteractive.astramarket.gui.button.NextPageButtonFactory
import ru.astrainteractive.astramarket.gui.button.PlayerItemButtonFactory
import ru.astrainteractive.astramarket.gui.button.PlayersSortButtonFactory
import ru.astrainteractive.astramarket.gui.button.PrevPageButtonFactory
import ru.astrainteractive.astramarket.market.domain.di.MarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule

internal interface MenuDrawerContext {
    val borderButtonRenderer: BorderButtonFactory
    val nextPageButtonFactory: NextPageButtonFactory
    val prevPageButtonFactory: PrevPageButtonFactory
    val expiredButtonFactory: ExpiredButtonFactory
    val aaucButtonFactory: AaucButtonFactory
    val auctionSortButtonFactory: AuctionSortButtonFactory
    val expiredMarketItemButtonFactory: ExpiredMarketItemButtonFactory
    val auctionMarketItemButtonFactory: AuctionMarketItemButtonFactory
    val playersSortButtonFactory: PlayersSortButtonFactory
    val playerItemButtonFactory: PlayerItemButtonFactory
    val backButtonFactory: BackButtonFactory

    class Default(
        coreModule: CoreModule,
        marketDomainModule: MarketDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketModule: PlayersMarketModule
    ) : MenuDrawerContext {
        private val dependency: ButtonFactoryDependency = ButtonFactoryDependency.Default(
            coreModule = coreModule,
            marketDomainModule = marketDomainModule,
            bukkitCoreModule = bukkitCoreModule,
            playersMarketModule = playersMarketModule
        )
        override val borderButtonRenderer = BorderButtonFactory(dependency)
        override val nextPageButtonFactory = NextPageButtonFactory(dependency)
        override val prevPageButtonFactory = PrevPageButtonFactory(dependency)
        override val expiredButtonFactory = ExpiredButtonFactory(dependency)
        override val aaucButtonFactory = AaucButtonFactory(dependency)
        override val auctionSortButtonFactory = AuctionSortButtonFactory(dependency)
        override val expiredMarketItemButtonFactory = ExpiredMarketItemButtonFactory(dependency)
        override val auctionMarketItemButtonFactory =
            AuctionMarketItemButtonFactory(dependency)
        override val playersSortButtonFactory = PlayersSortButtonFactory(dependency)
        override val playerItemButtonFactory = PlayerItemButtonFactory(dependency)
        override val backButtonFactory = BackButtonFactory(dependency)
    }
}
