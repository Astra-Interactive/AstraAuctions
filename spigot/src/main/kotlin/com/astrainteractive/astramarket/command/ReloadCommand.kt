package com.astrainteractive.astramarket.command

import com.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.klibs.kdi.getValue

fun CommandManager.reloadCommand() = plugin.registerCommand("amarketreload") {
    if (!PluginPermission.Reload.hasPermission(sender)) return@registerCommand
    sender.sendMessage(translation.reloadStarted)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadSuccess)
}
