package com.astrainteractive.astramarket.command

import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.utils.withEntry
import ru.astrainteractive.klibs.kdi.getValue

fun CommandManager.tabCompleter() = plugin.registerTabCompleter("amarket") {
    when (val size = args.size) {
        0 -> listOf("amarket")
        1 -> listOf("sell", "open", "expired").withEntry(args.last())
        2 -> listOf(translation.tabCompleterPrice).withEntry(args.last())
        3 -> listOf(translation.tabCompleterAmount).withEntry(args.last())
        else -> listOf<String>()
    }
}
