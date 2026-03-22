package ru.astrainteractive.astramarket.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.command.PaperMultiplatformCommands
import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.gui.router.di.BukkitRouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.service.di.WorkerModule
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

internal class RootModule(
    plugin: LifecyclePlugin
) : Logger by JUtiltLogger("AstraMarket-RootModule").withoutParentHandlers() {
    val coreModule: BukkitCoreModule = BukkitCoreModule.Default(plugin)

    val apiMarketModule: ApiMarketModule = ApiMarketModule.Default(
        dispatchers = coreModule.dispatchers,
        yamlStringFormat = coreModule.yamlStringFormat,
        dataFolder = coreModule.lifecyclePlugin.dataFolder,
        ioScope = coreModule.ioScope,
        default = { DatabaseConfiguration.H2(plugin.dataFolder.resolve("database").absolutePath) }
    )

    val marketViewModule: MarketViewModule = MarketViewModule.Default(
        coreModule = coreModule,
        apiMarketModule = apiMarketModule,
        marketDataModule = BukkitMarketDataModule(
            itemStackEncoder = coreModule.itemStackEncoder,
            stringSerializer = coreModule.kyoriKrate.cachedValue
        ),
        platformMarketDomainModule = BukkitMarketDomainModule(
            itemStackEncoder = coreModule.itemStackEncoder,
        )
    )

    val playersMarketViewModule: PlayersMarketViewModule = PlayersMarketViewModule.Default(
        coreModule = coreModule,
        apiMarketModule = apiMarketModule
    )

    val bukkitRouterModule: BukkitRouterModule = BukkitRouterModule(
        coreModule = coreModule,
        marketViewModule = marketViewModule,
        bukkitCoreModule = coreModule,
        playersMarketViewModule = playersMarketViewModule
    )

    val commandModule: CommandModule = CommandModule.Default(
        coreModule = coreModule,
        bukkitCoreModule = coreModule,
        bukkitRouterModule = bukkitRouterModule,
        marketViewModule = marketViewModule,
        multiplatformCommand = MultiplatformCommand(PaperMultiplatformCommands()),
        commandRegistrarContext = PaperCommandRegistrarContext(
            mainScope = coreModule.mainScope,
            plugin = plugin
        ),
    )

    val workerModule: WorkerModule = WorkerModule.Default(
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

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            lifecycles.forEach(Lifecycle::onEnable)
        },
        onReload = {
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
            lifecycles.forEach(Lifecycle::onReload)
        },
        onDisable = {
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
            HandlerList.unregisterAll(coreModule.lifecyclePlugin)
            lifecycles.forEach(Lifecycle::onDisable)
        }

    )
}
