package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.utils.Permissions
import org.bukkit.command.CommandSender

class ReloadCommand {
    companion object{
        fun execute(sender:CommandSender?){

            if (sender?.hasPermission(Permissions.reload)!=true)
                return
            sender?.sendMessage(AstraAuctions.translations.reloadStarted)
            AstraAuctions.instance.reloadPlugin()
            sender?.sendMessage(AstraAuctions.translations.reloadSuccess)
        }
    }
    val reload = AstraLibs.registerCommand("aaucreload") { sender, args ->
        execute(sender)
    }
}