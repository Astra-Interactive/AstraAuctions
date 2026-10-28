package ru.astrainteractive.astramarket.router.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.router.BukkitGuiRouter
import ru.astrainteractive.astramarket.router.api.GuiRouter

@Suppress("LongParameterList")
class BukkitRouterModule(
    coreModule: CoreModule,
    marketViewModule: MarketViewModule,
    objectEncoder: ObjectEncoder,
    playersMarketViewModule: PlayersMarketViewModule
) : RouterModule {
    override val router: GuiRouter = BukkitGuiRouter(
        coreModule = coreModule,
        marketViewModule = marketViewModule,
        objectEncoder = objectEncoder,
        playersMarketViewModule = playersMarketViewModule
    )
}
