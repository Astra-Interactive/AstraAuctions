package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.util.withEntry

fun CommandManager.tabCompleter() = plugin.registerTabCompleter("amarket") {
    when (val size = args.size) {
        0 -> listOf("amarket")
        1 -> listOf("sell", "open", "expired").withEntry(args.last())
        2 -> listOf(translation.tabCompleterPrice).withEntry(args.last())
        3 -> listOf(translation.tabCompleterAmount).withEntry(args.last())
        else -> listOf<String>()
    }
}
