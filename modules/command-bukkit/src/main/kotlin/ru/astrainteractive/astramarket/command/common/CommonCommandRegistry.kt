package ru.astrainteractive.astramarket.command.common

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.astramarket.command.common.di.CommonCommandDependencies
import ru.astrainteractive.astramarket.core.PluginPermission

internal class CommonCommandRegistry(dependencies: CommonCommandDependencies) :
    CommonCommandDependencies by dependencies {
    private fun createReloadCommand() {
        plugin.getCommand("amarketreload")?.setExecutor { sender, command, label, args ->
            if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) return@setExecutor true
            with(kyoriComponentSerializer) {
                sender.sendMessage(translation.general.reloadStarted.let(::toComponent))
                plugin.onReload()
                sender.sendMessage(translation.general.reloadSuccess.let(::toComponent))
            }
            true
        }
    }

    private fun createTabCompleter() = plugin.getCommand("amarket")?.setTabCompleter { sender, command, label, args ->
        when (val size = args.size) {
            0 -> listOf("amarket")
            1 -> listOf("sell", "open", "expired", "players").withEntry(args.last())
            2 -> {
                when (args.getOrNull(0)) {
                    "open", "expired" -> Bukkit.getOnlinePlayers().map(Player::getName)
                    "sell" -> listOf(translation.auction.tabCompleterAmount.raw).withEntry(args.last())
                    else -> emptyList()
                }
            }

            3 -> {
                when (args.getOrNull(0)) {
                    "sell" -> listOf(translation.auction.tabCompleterPrice.raw).withEntry(args.last())
                    else -> emptyList()
                }
            }

            else -> listOf<String>()
        }
    }

    fun register() {
        createTabCompleter()
        createReloadCommand()
    }
}
