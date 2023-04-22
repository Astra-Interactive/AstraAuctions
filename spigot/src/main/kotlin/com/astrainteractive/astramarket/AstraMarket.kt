package com.astrainteractive.astramarket

import CommandManager
import com.astrainteractive.astramarket.utils.AuctionExpireChecker
import com.astrainteractive.astramarket.modules.Modules
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.di.Singleton
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.events.GlobalEventListener
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.SharedInventoryClickEvent
import ru.astrainteractive.astralibs.utils.setupWithSpigot

/**
 * Initial class for your plugin
 */
class AstraMarket : JavaPlugin() {

    companion object : Singleton<AstraMarket>() {
        const val TAG = "AstraMarket"
    }

    private val _logger: Logger by Logger

    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.setupWithSpigot(TAG, this)
        instance = this
        Modules.bStats.value
        CommandManager()
        VaultEconomyProvider.onEnable()
        _logger.info(TAG, "Plugin enabled")
        AuctionExpireChecker.startAuctionChecker()
        GlobalEventListener.onEnable(this)
        SharedInventoryClickEvent.onEnable(this)
    }

    override fun onDisable() {
        AuctionExpireChecker.stopAuctionChecker()
        runBlocking { Modules.database.value.closeConnection() }
        HandlerList.unregisterAll(this)
        _logger.info("Plugin disabled", TAG)
        VaultEconomyProvider.onDisable()
        GlobalEventListener.onDisable()
        SharedInventoryClickEvent.onDisable()
    }

    fun reloadPlugin() {
        Modules.configFileManager.value.reload()
        Modules.configuration.reload()
        Modules.translation.reload()
    }

}


