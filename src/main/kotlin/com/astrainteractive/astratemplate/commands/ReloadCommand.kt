package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import org.bukkit.command.CommandSender

class ReloadCommand {
    companion object {
        fun execute(sender: CommandSender?) {

            if (sender?.hasPermission(Permissions.reload) != true)
                return
            sender?.sendMessage(Translation.reloadStarted)
            AstraMarket.instance.reloadPlugin()
            sender?.sendMessage(Translation.reloadSuccess)


        }
    }

    val reload = AstraLibs.registerCommand("amarketreload") { sender, args ->
        execute(sender)
    }
}



