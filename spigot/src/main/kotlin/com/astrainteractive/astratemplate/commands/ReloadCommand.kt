package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.Permissions
import org.bukkit.command.CommandSender
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

class ReloadCommand {
    companion object {
        fun execute(sender: CommandSender?) {
            val translation by TranslationModule

            if (sender?.hasPermission(Permissions.reload) != true)
                return
            sender?.sendMessage(translation.reloadStarted)
            AstraMarket.instance.reloadPlugin()
            sender?.sendMessage(translation.reloadSuccess)


        }
    }

    val reload = AstraLibs.instance.registerCommand("amarketreload") {
        execute(sender)
    }
}



