package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import org.bukkit.command.CommandSender
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.registerCommand

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

    val reload = AstraLibs.registerCommand("amarketreload") { sender, args ->
        execute(sender)
    }
}



