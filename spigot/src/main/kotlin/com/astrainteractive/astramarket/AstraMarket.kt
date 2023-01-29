package com.astrainteractive.astramarket

import CommandManager
import com.astrainteractive.astramarket.utils.AuctionExpireChecker
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.plugin.Files
import com.astrainteractive.astramarket.utils.*
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.GlobalEventManager
import ru.astrainteractive.astralibs.menu.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.utils.setupWithSpigot

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin() {
    final val TAG = "AstraMarket"

    /**
     * Static objects of this class
     * @see Translation
     */
    companion object {
        lateinit var instance: AstraMarket
            private set
    }




    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot("AstraAuctions")
        instance = this
        Modules.bStats.value
        CommandManager()
        VaultEconomyProvider.onEnable()
        Logger.log("Plugin enabled", TAG)
        AuctionExpireChecker.startAuctionChecker()
        SharedInventoryClickEvent.onEnable(GlobalEventManager)
    }

    override fun onDisable() {
        AuctionExpireChecker.stopAuctionChecker()
        runBlocking { Modules.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        Logger.log("Plugin disabled", TAG)
        VaultEconomyProvider.onDisable()
    }

    fun reloadPlugin() {
        Files.configFile.reload()
        Modules.configuration.reload()
        Modules.translation.reload()
    }

}


