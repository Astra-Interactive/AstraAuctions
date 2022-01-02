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

    /**
     * Class for handling all of your events
     *
     * Should be private
     */
    private lateinit var eventHandler: EventHandler

    /**
     * Command manager for your commands.
     *
     * You can create multiple managers.
     *
     * Should be private
     */
    private lateinit var commandManager: CommandManager



    /**
     * This method called when server starts.
     *
     * When server starts or PlugMan load plugin.
     */
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
    }

    /**
     * This method called when server is shutting down.
     *
     * Or when PlugMan disable plugin.
     */
    override fun onDisable() {
        eventHandler.onDisable()
        database.onDisable()
        HandlerList.unregisterAll(this)
    }

    /**
     * As it says, function for plugin reload
     */
    fun reloadPlugin() {
        onDisable()
        onEnable()
    }

}


