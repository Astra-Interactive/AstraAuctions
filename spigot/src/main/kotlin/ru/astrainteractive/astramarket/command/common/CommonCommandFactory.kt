package ru.astrainteractive.astramarket.command.common

import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.util.withEntry
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.command.common.di.CommonCommandDependencies
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.klibs.kdi.Factory

class CommonCommandFactory(dependencies: CommonCommandDependencies) :
    Factory<Unit>,
    CommonCommandDependencies by dependencies {
    private fun createReloadCommand() = plugin.registerCommand("amarketreload") {
        if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) return@registerCommand
        with(translationContext) {
            sender.sendMessage(translation.general.reloadStarted)
            (plugin as AstraMarket).onReload()
            sender.sendMessage(translation.general.reloadSuccess)
        }
    }

    private fun createTabCompleter() = plugin.registerTabCompleter("amarket") {
        when (val size = args.size) {
            0 -> listOf("amarket")
            1 -> listOf("sell", "open", "expired").withEntry(args.last())
            2 -> listOf(translation.auction.tabCompleterPrice.raw).withEntry(args.last())
            3 -> listOf(translation.auction.tabCompleterAmount.raw).withEntry(args.last())
            else -> listOf<String>()
        }
    }

    override fun create() {
        createTabCompleter()
        createReloadCommand()
    }
}
