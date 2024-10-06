package ru.astrainteractive.astramarket.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.LifecyclePlugin
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.di.util.ConnectionExt.toDBConnection
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule

internal interface RootModule {
    val lifecycle: Lifecycle

    val coreModule: BukkitCoreModule
    val apiMarketModule: ApiMarketModule
    val routerModule: RouterModule
    val marketModule: MarketModule
    val commandModule: CommandModule
    val playersMarketModule: PlayersMarketModule
    val workerModule: WorkerModule

    class Default(plugin: LifecyclePlugin) : RootModule, Logger by JUtiltLogger("RootModule") {
        override val coreModule: BukkitCoreModule by lazy {
            BukkitCoreModule.Default(plugin)
        }

        override val apiMarketModule: ApiMarketModule by lazy {
            val config = coreModule.config.cachedValue
            val (dbConnection, dbSyntax) = config.connection.toDBConnection()
            ApiMarketModule.Default(
                dbConnection = dbConnection,
                dbSyntax = dbSyntax,
                dispatchers = coreModule.dispatchers
            )
        }

        override val marketModule: MarketModule by lazy {
            MarketModule.Default(
                coreModule = coreModule,
                apiMarketModule = apiMarketModule,
                marketDataModule = BukkitMarketDataModule(
                    itemStackEncoder = coreModule.itemStackEncoder,
                    stringSerializer = coreModule.kyoriComponentSerializer.cachedValue
                ),
                platformMarketDomainModule = BukkitMarketDomainModule(
                    itemStackEncoder = coreModule.itemStackEncoder,
                )
            )
        }

        override val routerModule: RouterModule by lazy {
            RouterModule.Default(
                coreModule = coreModule,
                marketModule = marketModule,
                bukkitCoreModule = coreModule,
                playersMarketModule = playersMarketModule
            )
        }

        override val commandModule: CommandModule by lazy {
            CommandModule.Default(
                coreModule = coreModule,
                bukkitCoreModule = coreModule,
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

        private val lifecycles: List<Lifecycle>
            get() = listOf(
                coreModule.lifecycle,
                coreModule.lifecycle,
                apiMarketModule.lifecycle,
                commandModule.lifecycle,
                workerModule.lifecycle
            )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                lifecycles.forEach(Lifecycle::onEnable)
            },
            onReload = {
                Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
                lifecycles.forEach(Lifecycle::onReload)
            },
            onDisable = {
                Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
                HandlerList.unregisterAll(coreModule.plugin)
                lifecycles.forEach(Lifecycle::onDisable)
            }

        )
    }
}
