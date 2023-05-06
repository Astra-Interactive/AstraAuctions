@file:OptIn(UnsafeApi::class)

package com.astrainteractive.astramarket

import CommandManager
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.utils.AuctionExpireChecker
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

    private val vaultEconomyProvider by Modules.vaultEconomyProvider

    init {
        Modules.plugin.initialize(this)
    }

    override fun onEnable() {
        Modules.bStats.value
        CommandManager()
        vaultEconomyProvider
        AuctionExpireChecker.startAuctionChecker()
        GlobalEventListener.onEnable(this)
        GlobalInventoryClickEvent.onEnable(this)
    }

    override fun onDisable() {
        AuctionExpireChecker.stopAuctionChecker()
        runBlocking { Modules.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        GlobalInventoryClickEvent.onDisable()
    }

    fun reloadPlugin() {
        Modules.configFileManager.value.reload()
        Modules.configuration.reload()
        Modules.translation.reload()
    }
}
