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
        rootModule.plugin.initialize(this)
        rootModule.bStats.value
        CommandManager(CommandContainer.Default(rootModule))
        rootModule.economyProvider
        rootModule.inventoryClickEventListener.value.onEnable(this)
    }

    override fun onDisable() {
        runBlocking { rootModule.database.value.closeConnection() }
        HandlerList.unregisterAll(this)

        rootModule.scope.value.close()
        rootModule.inventoryClickEventListener.value.onDisable()
    }

    fun reloadPlugin() {
        rootModule.configuration.reload()
        rootModule.translation.reload()
    }
}
