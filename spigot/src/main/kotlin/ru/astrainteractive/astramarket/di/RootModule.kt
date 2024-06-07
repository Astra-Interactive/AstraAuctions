package ru.astrainteractive.astramarket.di

import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule

interface RootModule {
    val coreModule: CoreModule
    val bukkitCoreModule: BukkitCoreModule
    val apiMarketModule: ApiMarketModule
    val routerModule: RouterModule
    val marketModule: MarketModule
    val commandModule: CommandModule
    val playersMarketModule: PlayersMarketModule
    val workerModule: WorkerModule
}
