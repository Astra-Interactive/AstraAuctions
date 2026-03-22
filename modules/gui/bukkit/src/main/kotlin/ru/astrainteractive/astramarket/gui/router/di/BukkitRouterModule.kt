package ru.astrainteractive.astramarket.gui.router.di

import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.GuiRouterImpl
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule

@Suppress("LongParameterList")
class BukkitRouterModule(
    coreModule: CoreModule,
    marketViewModule: MarketViewModule,
    bukkitCoreModule: BukkitCoreModule,
    playersMarketViewModule: PlayersMarketViewModule
) : RouterModule {
    override val router: GuiRouter = GuiRouterImpl(
        coreModule = coreModule,
        marketViewModule = marketViewModule,
        bukkitCoreModule = bukkitCoreModule,
        playersMarketViewModule = playersMarketViewModule
    )
}
