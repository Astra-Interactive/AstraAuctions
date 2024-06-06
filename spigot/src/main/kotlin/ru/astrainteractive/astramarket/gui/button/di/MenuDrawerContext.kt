package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.gui.button.AaucButtonFactory
import ru.astrainteractive.astramarket.gui.button.AuctionMarketItemButtonFactory
import ru.astrainteractive.astramarket.gui.button.BorderButtonFactory
import ru.astrainteractive.astramarket.gui.button.ExpiredButtonFactory
import ru.astrainteractive.astramarket.gui.button.ExpiredMarketItemButtonFactory
import ru.astrainteractive.astramarket.gui.button.NextPageButtonFactory
import ru.astrainteractive.astramarket.gui.button.PrevPageButtonFactory
import ru.astrainteractive.astramarket.gui.button.SortButtonFactory

interface MenuDrawerContext {
    val borderButtonRenderer: BorderButtonFactory
    val nextPageButtonFactory: NextPageButtonFactory
    val prevPageButtonFactory: PrevPageButtonFactory
    val expiredButtonFactory: ExpiredButtonFactory
    val aaucButtonFactory: AaucButtonFactory
    val sortButtonFactory: SortButtonFactory
    val expiredMarketItemButtonFactory: ExpiredMarketItemButtonFactory
    val auctionMarketItemButtonFactory: AuctionMarketItemButtonFactory

    class Default(
        coreModule: CoreModule,
        sharedDomainModule: SharedDomainModule,
        bukkitCoreModule: BukkitCoreModule,
    ) : MenuDrawerContext {
        private val dependency: ButtonFactoryDependency = ButtonFactoryDependency.Default(
            coreModule = coreModule,
            sharedDomainModule = sharedDomainModule,
            bukkitCoreModule = bukkitCoreModule
        )
        override val borderButtonRenderer = BorderButtonFactory(dependency)
        override val nextPageButtonFactory = NextPageButtonFactory(dependency)
        override val prevPageButtonFactory = PrevPageButtonFactory(dependency)
        override val expiredButtonFactory = ExpiredButtonFactory(dependency)
        override val aaucButtonFactory = AaucButtonFactory(dependency)
        override val sortButtonFactory = SortButtonFactory(dependency)
        override val expiredMarketItemButtonFactory = ExpiredMarketItemButtonFactory(dependency)
        override val auctionMarketItemButtonFactory = AuctionMarketItemButtonFactory(dependency)
    }
}
