package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.utils.Permissions
import org.bukkit.command.CommandSender

class ReloadCommand {
    companion object {
        fun execute(sender: CommandSender?) {

            if (sender?.hasPermission(Permissions.reload) != true)
                return
            sender?.sendMessage(AstraMarket.translations.reloadStarted)
            AstraMarket.instance.reloadPlugin()
            sender?.sendMessage(AstraMarket.translations.reloadSuccess)


        }
    }

    val reload = AstraLibs.registerCommand("amarketreload") { sender, args ->
        execute(sender)
    }
}



