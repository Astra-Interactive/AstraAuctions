package ru.astrainteractive.astramarket

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.AstraMarketPlugin
import ru.astrainteractive.astramarket.di.RootModule

/**
 * Initial class for your plugin
 */
class AstraMarket : AstraMarketPlugin() {

    private val rootModule = RootModule.Default()
    private val lifecycle: List<Lifecycle>
        get() = listOf(
            rootModule.coreModule.lifecycle,
            rootModule.bukkitCoreModule.lifecycle,
            rootModule.apiMarketModule.lifecycle,
            rootModule.commandModule.lifecycle,
            rootModule.workerModule.lifecycle
        )

    override fun onEnable() {
        rootModule.bukkitCoreModule.plugin.initialize(this)
        lifecycle.forEach(Lifecycle::onEnable)
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
        HandlerList.unregisterAll(this)
        lifecycle.forEach(Lifecycle::onDisable)
    }

    override fun onReload() {
        Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
        lifecycle.forEach(Lifecycle::onReload)
    }
}
