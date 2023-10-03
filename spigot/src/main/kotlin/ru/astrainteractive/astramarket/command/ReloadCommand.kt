package ru.astrainteractive.astramarket.command

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astramarket.command.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.plugin.PluginPermission

fun CommandManager.reloadCommand() = plugin.registerCommand("amarketreload") {
    (sender as? Player)?.let { player ->
        if (!permissionManager.hasPermission(player.uniqueId, PluginPermission.Reload)) return@registerCommand
    }
    stringSerializer.sendMessage(translation.general.reloadStarted, sender)
    plugin.reloadPlugin()
    stringSerializer.sendMessage(translation.general.reloadSuccess, sender)
}
