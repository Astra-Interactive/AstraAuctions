package ru.astrainteractive.astramarket.gui.bukkit.router.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.api.market.di.MarketViewModule
import ru.astrainteractive.astramarket.gui.api.player.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.gui.api.router.api.GuiRouter
import ru.astrainteractive.astramarket.gui.api.router.di.RouterModule
import ru.astrainteractive.astramarket.gui.bukkit.router.BukkitGuiRouter

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
