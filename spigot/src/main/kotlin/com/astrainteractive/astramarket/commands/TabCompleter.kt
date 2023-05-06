package com.astrainteractive.astramarket.commands

import CommandManager
import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.withEntry

fun CommandManager.tabCompleter(
    plugin: JavaPlugin
) = plugin.registerTabCompleter("amarket") {
    val translation by RootModuleImpl.translation
    when (val size = args.size) {
        0 -> listOf("amarket")
        1 -> listOf("sell", "open", "expired").withEntry(args.last())
        2 -> listOf(translation.tabCompleterPrice).withEntry(args.last())
        3 -> listOf(translation.tabCompleterAmount).withEntry(args.last())
        else -> listOf<String>()
    }
}
