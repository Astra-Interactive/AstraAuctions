@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astramarket

import CommandManager
import com.astrainteractive.astramarket.di.impl.CommandsModuleImpl
import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import com.astrainteractive.astramarket.util.AuctionExpireChecker
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.event.GlobalInventoryClickEvent

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin() {

    private val vaultEconomyProvider by RootModuleImpl.vaultEconomyProvider

    init {
        RootModuleImpl.plugin.initialize(this)
    }

    override fun onEnable() {
        RootModuleImpl.bStats.value
        CommandManager(this, CommandsModuleImpl)
        vaultEconomyProvider
        AuctionExpireChecker.startAuctionChecker()
        GlobalEventListener.onEnable(this)
        GlobalInventoryClickEvent.onEnable(this)
    }

    override fun onDisable() {
        AuctionExpireChecker.stopAuctionChecker()
        runBlocking { RootModuleImpl.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        GlobalInventoryClickEvent.onDisable()
    }

    fun reloadPlugin() {
        RootModuleImpl.configFileManager.value.reload()
        RootModuleImpl.configuration.reload()
        RootModuleImpl.translation.reload()
    }
}
