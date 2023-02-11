package com.astrainteractive.astramarket.commands

import CommandManager
import com.astrainteractive.astramarket.modules.Modules
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.withEntry

fun CommandManager.tabCompleter() = ru.astrainteractive.astralibs.AstraLibs.instance.registerTabCompleter("amarket") {
    val translation by Modules.translation
    when (val size = args.size) {
        0 -> listOf("amarket")
        1 -> listOf("sell", "open", "expired").withEntry(args.last())
        2 -> listOf(translation.tabCompleterPrice).withEntry(args.last())
        3 -> listOf(translation.tabCompleterAmount).withEntry(args.last())
        else -> listOf<String>()
    }
}

