package com.astrainteractive.astratemplate

//import com.makeevrserg.empiretemplate.database.EmpireDatabase
import CommandManager
import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astratemplate.events.EventHandler
import com.astrainteractive.astratemplate.sqldatabase.Database
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.Files
import com.astrainteractive.astratemplate.utils.VaultHook
import com.astrainteractive.astratemplate.utils.config.AuctionConfig
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

/**
 * Initial class for your plugin
 */
class AstraAuctions : JavaPlugin() {

    /**
     * Static objects of this class
     * @see Translation
     */
    companion object {
        lateinit var instance: AstraAuctions
            private set
        lateinit var translations: Translation
            private set
        lateinit var empireFiles: Files
            private set
        lateinit var pluginConfig: AuctionConfig
            private set
        public lateinit var database: Database
            private set
    }


    private lateinit var eventHandler: EventHandler

    private lateinit var commandManager: CommandManager


    override fun onEnable() {
        AstraLibs.create(this)
        Logger.init("AstraAuctions")
        instance = this
        translations = Translation()
        empireFiles = Files()
        eventHandler = EventHandler()
        commandManager = CommandManager()
        pluginConfig = AuctionConfig.load()
        database = Database().apply { onEnable() }
        VaultHook()
        Logger.log("Plugin enabled","AstraAuctions")
    }

    override fun onDisable() {
        eventHandler.onDisable()
        database.onDisable()
        HandlerList.unregisterAll(this)
        Logger.log("Plugin disabled","AstraAuctions")
    }

    fun reloadPlugin() {
        onDisable()
        onEnable()
    }

}


