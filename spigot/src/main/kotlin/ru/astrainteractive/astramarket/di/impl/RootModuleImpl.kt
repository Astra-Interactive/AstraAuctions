package ru.astrainteractive.astramarket.di.impl

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProviderFactory
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.di.util.ConnectionExt.toDBConnection
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class RootModuleImpl : RootModule {
    override val bukkitCoreModule: BukkitCoreModule by lazy {
        BukkitCoreModuleImpl()
    }
    override val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = bukkitCoreModule.plugin.value.dataFolder,
            dispatchers = DefaultBukkitDispatchers(bukkitCoreModule.plugin.value),
            economyProvider = EconomyProviderFactory(bukkitCoreModule.plugin.value).create()
        )
    }
    override val apiMarketModule: ApiMarketModule by Provider {
        val config by coreModule.config
        val (dbConnection, dbSyntax) = config.connection.toDBConnection()
        ApiMarketModule.Default(
            dbConnection = dbConnection,
            dbSyntax = dbSyntax,
            dispatchers = coreModule.dispatchers
        )
    }

    override val marketModule: MarketModule by Provider {
        MarketModule.Default(
            coreModule = coreModule,
            apiMarketModule = apiMarketModule,
            marketDataModuleFactory = {
                BukkitMarketDataModule(
                    encoder = bukkitCoreModule.encoder.value,
                    stringSerializer = bukkitCoreModule.kyoriComponentSerializer.value
                )
            },
            platformMarketDomainModuleFactory = {
                BukkitMarketDomainModule(
                    encoder = bukkitCoreModule.encoder.value,
                )
            }
        )
    }
    override val routerModule: RouterModule by Provider {
        RouterModule.Default(this)
    }
    override val commandModule: CommandModule by lazy {
        CommandModule.Default(this)
    }
    override val playersMarketModule: PlayersMarketModule by lazy {
        PlayersMarketModule.Default(
            coreModule = coreModule,
            apiMarketModule = apiMarketModule
        )
    }
    override val workerModule: WorkerModule by lazy {
        WorkerModule.Default(
            apiMarketModule = apiMarketModule,
            coreModule = coreModule
        )
    }
}
