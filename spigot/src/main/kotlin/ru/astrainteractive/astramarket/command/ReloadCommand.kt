package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.core.PluginPermission

fun CommandManager.reloadCommand() = plugin.registerCommand("amarketreload") {
    if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) return@registerCommand
    sender.sendMessage(translation.general.reloadStarted)
    plugin.onReload()
    sender.sendMessage(translation.general.reloadSuccess)
}
