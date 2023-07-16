@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astramarket

import com.astrainteractive.astramarket.command.CommandManager
import com.astrainteractive.astramarket.di.impl.CommandsModuleImpl
import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent
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
        CommandManager(CommandsModuleImpl(rootModule))
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
