package ru.astrainteractive.astramarket.di

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.util.ConnectionExt.toDBConnection
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal interface RootModule {
    val coreModule: CoreModule
    val bukkitCoreModule: BukkitCoreModule
    val apiMarketModule: ApiMarketModule
    val routerModule: RouterModule
    val marketModule: MarketModule
    val commandModule: CommandModule
    val playersMarketModule: PlayersMarketModule
    val workerModule: WorkerModule

    class Default : RootModule, Logger by JUtiltLogger("RootModule") {
        override val bukkitCoreModule: BukkitCoreModule by lazy {
            BukkitCoreModule.Default()
        }

        override val coreModule: CoreModule by lazy {
            CoreModule.Default(
                dataFolder = bukkitCoreModule.plugin.value.dataFolder,
                dispatchers = DefaultBukkitDispatchers(bukkitCoreModule.plugin.value),
                getEconomyProvider = getEconomyProviderById@{ currencyId ->
                    val registrations = Bukkit.getServer().servicesManager.getRegistrations(Economy::class.java)
                    if (currencyId == null) {
                        return@getEconomyProviderById VaultEconomyProvider(
                            bukkitCoreModule.plugin.value
                        )
                    }
                    val specificEconomyProvider = registrations
                        .firstOrNull { it.provider.currencyNameSingular() == currencyId }
                        ?.provider
                        ?.let(::VaultEconomyProvider)
                    if (specificEconomyProvider == null) {
                        error { "#economyProvider could not find economy with currency: $currencyId" }
                    } else {
                        return@getEconomyProviderById specificEconomyProvider
                    }
                    error("EconomyProvider could not find economy with currency: $currencyId")
                }
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
                        itemStackEncoder = bukkitCoreModule.itemStackEncoder,
                        stringSerializer = bukkitCoreModule.kyoriComponentSerializer.value
                    )
                },
                platformMarketDomainModuleFactory = {
                    BukkitMarketDomainModule(
                        itemStackEncoder = bukkitCoreModule.itemStackEncoder,
                    )
                }
            )
        }

        override val routerModule: RouterModule by Provider {
            RouterModule.Default(
                coreModule = coreModule,
                marketModule = marketModule,
                bukkitCoreModule = bukkitCoreModule,
                playersMarketModule = playersMarketModule
            )
        }

        override val commandModule: CommandModule by lazy {
            CommandModule.Default(
                coreModule = coreModule,
                bukkitCoreModule = bukkitCoreModule,
                routerModule = routerModule,
                marketModule = marketModule
            )
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
}
