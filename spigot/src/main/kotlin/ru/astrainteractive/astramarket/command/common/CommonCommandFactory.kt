package ru.astrainteractive.astramarket.command.common

import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.util.StringListExt.withEntry
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.command.common.di.CommonCommandDependencies
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.klibs.kdi.Factory

class CommonCommandFactory(dependencies: CommonCommandDependencies) :
    Factory<Unit>,
    CommonCommandDependencies by dependencies {
    private fun createReloadCommand() {
        plugin.getCommand("amarketreload")?.setExecutor { sender, command, label, args ->
            if (!sender.toPermissible().hasPermission(PluginPermission.Reload)) return@setExecutor true
            with(kyoriComponentSerializer) {
                sender.sendMessage(translation.general.reloadStarted.let(::toComponent))
                (plugin as AstraMarket).onReload()
                sender.sendMessage(translation.general.reloadSuccess.let(::toComponent))
            }
            true
        }
    }

    private fun createTabCompleter() = plugin.getCommand("amarket")?.setTabCompleter { sender, command, label, args ->
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
