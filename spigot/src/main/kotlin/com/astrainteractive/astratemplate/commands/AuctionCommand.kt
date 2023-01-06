package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astratemplate.api.use_cases.CreateAuctionUseCase
import com.astrainteractive.astratemplate.commands.AuctionCommand.Arguments.Companion.getArgumentString
import com.astrainteractive.astratemplate.gui.AuctionGui
import com.astrainteractive.astratemplate.gui.ExpiredAuctionGui
import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.playSound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.registerTabCompleter
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.withEntry

class AuctionCommand {
    private val translation by TranslationModule
    private val config by ConfigModule

    val tabCompleter = AstraLibs.instance.registerTabCompleter("amarket") {
        if (args.isEmpty())
            return@registerTabCompleter listOf("amarket")
        if (args.size == 1)
            return@registerTabCompleter listOf("sell", "open", "expired").withEntry(args.last())
        if (args.size == 2)
            return@registerTabCompleter listOf(translation.tabCompleterPrice).withEntry(args.last())
        if (args.size == 3)
            return@registerTabCompleter listOf(translation.tabCompleterAmount).withEntry(args.last())

        return@registerTabCompleter listOf<String>()
    }

    private fun CommandSender.checkPlayer(): Boolean = if (this !is Player) {
        sendMessage(translation.onlyForPlayers)
        false
    } else {
        true
    }

    private fun CommandSender.checkPermission(permission: String): Boolean = if (!this.hasPermission(permission)) {
        sendMessage(translation.noPermissions)
        false
    } else {
        true
    }

    private class Arguments {
        companion object {
            val cmd: Pair<String, Int>
                get() = "cmd" to 0
            val price: Pair<String, Int>
                get() = "price" to 1
            val amount: Pair<String, Int>
                get() = "amount" to 2

            fun get(argument: Pair<String, Int>, args: Array<out String>): String? {
                return args.getOrNull(argument.second)
            }

            fun Array<out String>.getArgumentString(argument: Pair<String, Int>) = getOrNull(argument.second)
        }
    }

    val aauc = AstraLibs.instance.registerCommand("amarket") {
        val sender = this.sender
        val cmd = args.getArgumentString(Arguments.cmd)
        if (cmd.equals("reload", ignoreCase = true)) {
            ReloadCommand.execute(sender)
            return@registerCommand
        }
        if (!sender.checkPlayer())
            return@registerCommand
        sender as Player
        when (cmd) {
            "sell" -> sell(sender, args)
            "expired" -> PluginScope.launch(Dispatchers.IO) {
                ExpiredAuctionGui(sender).open()
            }
            "open", null -> PluginScope.launch(Dispatchers.IO) {
                AuctionGui(sender).open()
            }

        }
    }

    private fun sell(player: Player, args: Array<out String>) {
        if (!player.checkPermission(Permissions.sell))
            return
        val perm = player.effectivePermissions.firstOrNull { it.permission.startsWith(Permissions.sellMax) }
        val maxAuctionsAllowed = perm?.permission?.replace(Permissions.sellMax + ".", "")?.toIntOrNull()
            ?: config.auction.maxAuctionPerPlayer ?: 1

        val price = args.getArgumentString(Arguments.price)?.toFloatOrNull() ?: run {
            player.sendMessage(translation.wrongArgs)
            player.playSound(config.sounds.fail)
            return
        }
        val amount = args.getArgumentString(Arguments.amount)?.toIntOrNull() ?: 1

        val item = player.inventory.itemInMainHand
        PluginScope.launch(Dispatchers.IO) {
            CreateAuctionUseCase()(
                CreateAuctionUseCase.Params(
                    maxAuctionsAllowed = maxAuctionsAllowed,
                    player = player,
                    price = price,
                    amount = amount,
                    item = item
                )
            )
        }


    }
}



