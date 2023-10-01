package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astramarket.plugin.PluginPermission

fun CommandManager.reloadCommand() = plugin.registerCommand("amarketreload") {
    if (!PluginPermission.Reload.hasPermission(sender)) return@registerCommand
    sender.sendMessage(translation.reloadStarted)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadSuccess)
}
