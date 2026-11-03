@file:Suppress("RootModuleConstruction")

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
import ru.astrainteractive.astramarket.data.exposed.di.ExposedModule
import ru.astrainteractive.astramarket.gui.api.market.di.MarketViewModule
import ru.astrainteractive.astramarket.gui.api.player.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.gui.bukkit.market.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.gui.bukkit.market.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.gui.bukkit.router.di.BukkitRouterModule
import ru.astrainteractive.astramarket.service.di.WorkerModule
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration

internal class RootModule(
    plugin: LifecyclePlugin
) : Logger by JUtiltLogger("AstraMarket-RootModule").withoutParentHandlers() {
    val coreModule: BukkitCoreModule = BukkitCoreModule.Default(
        lifecyclePlugin = plugin,
        multiplatformCommand = MultiplatformCommand(PaperMultiplatformCommands())
    )

    val exposedModule: ExposedModule = ExposedModule(
        dispatchers = coreModule.dispatchers,
        yamlStringFormat = coreModule.yamlStringFormat,
        dataFolder = coreModule.lifecyclePlugin.dataFolder,
        ioScope = coreModule.ioScope,
        default = { DatabaseConfiguration.H2(plugin.dataFolder.resolve("database").absolutePath) }
    )

    val marketViewModule: MarketViewModule = MarketViewModule.Default(
        coreModule = coreModule,
        marketRepository = exposedModule.marketRepository,
        marketDataModule = BukkitMarketDataModule(
            objectEncoder = coreModule.objectEncoder
        ),
        platformMarketDomainModule = BukkitMarketDomainModule(
            objectEncoder = coreModule.objectEncoder,
        )
    )

    val playersMarketViewModule: PlayersMarketViewModule = PlayersMarketViewModule.Default(
        coreModule = coreModule,
        marketRepository = exposedModule.marketRepository
    )

    val bukkitRouterModule: BukkitRouterModule = BukkitRouterModule(
        coreModule = coreModule,
        marketViewModule = marketViewModule,
        objectEncoder = coreModule.objectEncoder,
        playersMarketViewModule = playersMarketViewModule
    )

    val commandModule: CommandModule = CommandModule.Default(
        coreModule = coreModule,
        objectEncoder = coreModule.objectEncoder,
        bukkitRouterModule = bukkitRouterModule,
        marketViewModule = marketViewModule,
        commandRegistrarContext = PaperCommandRegistrarContext(
            mainScope = coreModule.unconfinedScope,
            plugin = plugin
        ),
    )

    val workerModule: WorkerModule = WorkerModule.Default(
        marketRepository = exposedModule.marketRepository,
        coreModule = coreModule
    )

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            exposedModule.lifecycle,
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
