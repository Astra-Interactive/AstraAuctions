package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.api.use_cases.CreateAuctionUseCase
import com.astrainteractive.astratemplate.commands.AuctionCommand.Arguments.Companion.getArgumentString
import com.astrainteractive.astratemplate.gui.AuctionGui
import com.astrainteractive.astratemplate.gui.ExpiredAuctionGui
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.playSound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class AuctionCommand {


    val tabCompleter = AstraLibs.registerTabCompleter("amarket") { sender, args ->
        if (args.isEmpty())
            return@registerTabCompleter listOf("amarket")
        if (args.size == 1)
            return@registerTabCompleter listOf("sell", "open", "expired").withEntry(args.last())
        if (args.size == 2)
            return@registerTabCompleter listOf(Translation.tabCompleterPrice).withEntry(args.last())
        if (args.size == 3)
            return@registerTabCompleter listOf(Translation.tabCompleterAmount).withEntry(args.last())

        return@registerTabCompleter listOf<String>()
    }

    private fun CommandSender.checkPlayer(): Boolean = if (this !is Player) {
        sendMessage(Translation.onlyForPlayers)
        false
    } else {
        true
    }

    private fun CommandSender.checkPermission(permission: String): Boolean = if (!this.hasPermission(permission)) {
        sendMessage(Translation.noPermissions)
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

    val aauc = AstraLibs.registerCommand("amarket") { sender, args ->
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
            "expired" -> AsyncHelper.launch(Dispatchers.IO) {
                ExpiredAuctionGui(AstraPlayerMenuUtility(sender)).open()
            }
            "open", null -> AsyncHelper.launch(Dispatchers.IO) {
                AuctionGui(AstraPlayerMenuUtility(sender)).open()
            }

        }
    }

    private fun sell(player: Player, args: Array<out String>) {
        if (!player.checkPermission(Permissions.sell))
            return
        val perm = player.effectivePermissions.firstOrNull { it.permission.startsWith(Permissions.sellMax) }
        val maxAuctionsAllowed = perm?.permission?.replace(Permissions.sellMax + ".", "")?.toIntOrNull()
            ?: AstraMarket.pluginConfig.auction.maxAuctionPerPlayer ?: 1

        val price = args.getArgumentString(Arguments.price)?.toFloatOrNull() ?: run {
            player.sendMessage(Translation.wrongArgs)
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            return
        }
        val amount = args.getArgumentString(Arguments.amount)?.toIntOrNull() ?: 1

        val item = player.inventory.itemInMainHand
        AsyncHelper.launch {
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



