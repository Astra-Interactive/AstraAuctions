package com.astrainteractive.astratemplate

import CommandManager
import com.astrainteractive.astratemplate.api.AuctionExpireChecker
import com.astrainteractive.astratemplate.modules.BStatsModule
import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.DatabaseModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
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
        BStatsModule.value
        CommandManager()
        VaultEconomyProvider.onEnable()
        Logger.log("Plugin enabled", TAG)
        AuctionExpireChecker.startAuctionChecker()
    }

    override fun onDisable() {
        AuctionExpireChecker.stopAuctionChecker()
        runBlocking { DatabaseModule.value.closeConnection() }
        HandlerList.unregisterAll(this)
        Logger.log("Plugin disabled", TAG)
        VaultEconomyProvider.onDisable()
    }

    fun reloadPlugin() {
        Files.configFile.reload()
        ConfigModule.reload()
        TranslationModule.reload()
    }

}


