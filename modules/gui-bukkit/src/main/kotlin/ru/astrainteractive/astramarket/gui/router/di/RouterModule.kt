package ru.astrainteractive.astramarket.gui.router.di

import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.GuiRouterImpl
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule

interface RouterModule {
    val router: GuiRouter

    @Suppress("LongParameterList")
    class Default(
        private val coreModule: CoreModule,
        private val marketModule: MarketModule,
        private val bukkitCoreModule: BukkitCoreModule,
        private val playersMarketModule: PlayersMarketModule
    ) : RouterModule {
        override val router: GuiRouter = GuiRouterImpl(
            coreModule = coreModule,
            marketModule = marketModule,
            bukkitCoreModule = bukkitCoreModule,
            playersMarketModule = playersMarketModule
        )
    }
}
