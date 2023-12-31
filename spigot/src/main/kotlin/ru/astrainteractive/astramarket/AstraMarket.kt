package ru.astrainteractive.astramarket

import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.CommandManager
import ru.astrainteractive.astramarket.command.di.CommandContainer
import ru.astrainteractive.astramarket.di.impl.RootModuleImpl

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin(), Lifecycle {

    private val rootModule = RootModuleImpl()
    private val lifecycle: List<Lifecycle>
        get() = listOf(
            rootModule.coreModule.lifecycle,
            rootModule.bukkitCoreModule.lifecycle,
            rootModule.apiMarketModule.lifecycle
        )

    override fun onEnable() {
        rootModule.bukkitCoreModule.plugin.initialize(this)
        CommandManager(CommandContainer.Default(rootModule))
        lifecycle.forEach(Lifecycle::onEnable)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        lifecycle.forEach(Lifecycle::onDisable)
    }

    override fun onReload() {
        lifecycle.forEach(Lifecycle::onReload)
    }
}
