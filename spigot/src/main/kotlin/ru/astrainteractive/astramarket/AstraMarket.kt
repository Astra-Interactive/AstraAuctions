@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.astramarket

import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.event.GlobalEventListener
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent
import ru.astrainteractive.astramarket.command.CommandManager
import ru.astrainteractive.astramarket.command.di.CommandsModule
import ru.astrainteractive.astramarket.di.impl.RootModuleImpl
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.getValue

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin() {

    private val rootModuleReloadable = Reloadable {
        RootModuleImpl()
    }
    private val rootModule by rootModuleReloadable

    override fun onEnable() {
        rootModule.plugin.initialize(this)
        rootModule.bStats.value
        CommandManager(CommandsModule.Default(rootModule))
        rootModule.vaultEconomyProvider
        GlobalEventListener.onEnable(this)
        GlobalInventoryClickEvent.onEnable(this)
    }

    override fun onDisable() {
        runBlocking { rootModule.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        GlobalInventoryClickEvent.onDisable()
    }

    fun reloadPlugin() {
        rootModule.configFileManager.value.reload()
        rootModule.configuration.reload()
        rootModule.translation.reload()
    }
}
