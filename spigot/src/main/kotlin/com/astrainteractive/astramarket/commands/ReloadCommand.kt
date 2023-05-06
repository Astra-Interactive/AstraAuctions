package com.astrainteractive.astramarket.commands

import CommandManager
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

private val plugin by Modules.plugin
fun CommandManager.reloadCommand() = plugin.registerCommand("amarketreload") {
    val translation by Modules.translation
    if (!PluginPermission.Reload.hasPermission(sender)) return@registerCommand
    sender.sendMessage(translation.reloadStarted)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadSuccess)
}
