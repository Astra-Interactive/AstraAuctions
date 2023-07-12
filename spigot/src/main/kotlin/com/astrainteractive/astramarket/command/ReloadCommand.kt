package com.astrainteractive.astramarket.command

import CommandManager
import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.command.di.CommandsModule
import com.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

fun CommandManager.reloadCommand(
    plugin: AstraMarket,
    module: CommandsModule
) = plugin.registerCommand("amarketreload") {
    val translation by module.translation
    if (!PluginPermission.Reload.hasPermission(sender)) return@registerCommand
    sender.sendMessage(translation.reloadStarted)
    plugin.reloadPlugin()
    sender.sendMessage(translation.reloadSuccess)
}
