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
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule

internal interface RootModule {
    val lifecycle: Lifecycle

    val coreModule: BukkitCoreModule
    val apiMarketModule: ApiMarketModule
    val routerModule: RouterModule
    val marketViewModule: MarketViewModule
    val commandModule: CommandModule
    val playersMarketViewModule: PlayersMarketViewModule
    val workerModule: WorkerModule

    class Default(plugin: LifecyclePlugin) : RootModule, Logger by JUtiltLogger("AstraMarket-RootModule") {
        override val coreModule: BukkitCoreModule = BukkitCoreModule.Default(plugin)

        override val apiMarketModule: ApiMarketModule = ApiMarketModule.Default(
            dispatchers = coreModule.dispatchers,
            yamlStringFormat = coreModule.yamlStringFormat,
            dataFolder = coreModule.plugin.dataFolder
        )

        override val marketViewModule: MarketViewModule = MarketViewModule.Default(
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

        override val playersMarketViewModule: PlayersMarketViewModule = PlayersMarketViewModule.Default(
            coreModule = coreModule,
            apiMarketModule = apiMarketModule
        )

        override val routerModule: RouterModule = RouterModule.Default(
            coreModule = coreModule,
            marketViewModule = marketViewModule,
            bukkitCoreModule = coreModule,
            playersMarketViewModule = playersMarketViewModule
        )

        override val commandModule: CommandModule = CommandModule.Default(
            coreModule = coreModule,
            bukkitCoreModule = coreModule,
            routerModule = routerModule,
            marketViewModule = marketViewModule
        )

        override val workerModule: WorkerModule = WorkerModule.Default(
            apiMarketModule = apiMarketModule,
            coreModule = coreModule
        )

        private val lifecycles: List<Lifecycle>
            get() = listOf(
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
