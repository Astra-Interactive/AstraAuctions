package ru.astrainteractive.astramarket

import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astramarket.command.CommandManager
import ru.astrainteractive.astramarket.command.di.CommandContainer
import ru.astrainteractive.astramarket.di.impl.RootModuleImpl

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin() {

    private val rootModule = RootModuleImpl()

    override fun onEnable() {
        rootModule.bukkitCoreModule.plugin.initialize(this)
        rootModule.bukkitCoreModule.bStats.value
        CommandManager(CommandContainer.Default(rootModule))
        rootModule.bukkitCoreModule.economyProvider
        rootModule.bukkitCoreModule.inventoryClickEventListener.value.onEnable(this)
    }

    override fun onDisable() {
        runBlocking { rootModule.dataModule.database.closeConnection() }
        HandlerList.unregisterAll(this)

        rootModule.bukkitCoreModule.scope.value.close()
        rootModule.bukkitCoreModule.inventoryClickEventListener.value.onDisable()
    }

    fun reloadPlugin() {
        rootModule.bukkitCoreModule.configuration.reload()
        rootModule.bukkitCoreModule.translation.reload()
    }
}
