package com.astrainteractive.astramarket.commands

import CommandManager
import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.plugin.Permission
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

fun CommandManager.reloadCommand() = AstraLibs.instance.registerCommand("amarketreload") {
    val translation by Modules.translation
    if (!Permission.Reload.hasPermission(sender)) return@registerCommand
    sender.sendMessage(translation.reloadStarted)
    AstraMarket.instance.reloadPlugin()
    sender.sendMessage(translation.reloadSuccess)
}



