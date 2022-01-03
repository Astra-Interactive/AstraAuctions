package com.astrainteractive.astratemplate.commands

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.registerCommand
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.commands.AuctionCommand.Arguments.Companion.getArgumentString
import com.astrainteractive.astratemplate.gui.AuctionGui
import com.astrainteractive.astratemplate.sqldatabase.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.sqldatabase.entities.Callback
import com.astrainteractive.astratemplate.utils.AsyncTask
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.playSound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.Exception
import kotlin.math.min

class AuctionCommand : AsyncTask {


    private fun CommandSender.checkPlayer(): Boolean = if (this !is Player) {
        sendMessage(Translation.instanse.onlyForPlayers)
        false
    } else {
        true
    }

    private fun CommandSender.checkPermission(permission: String): Boolean = if (!this.hasPermission(permission)) {
        sendMessage(Translation.instanse.noPermissions)
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

    val aauc = AstraLibs.registerCommand("aauc") { sender, args ->
        val cmd = args.getArgumentString(Arguments.cmd)
        if (cmd.equals("reload",ignoreCase = true)){
            ReloadCommand.execute(sender)
            return@registerCommand
        }
        if (!sender.checkPermission(Permissions.sell))
            return@registerCommand
        if (!sender.checkPlayer())
            return@registerCommand
        sender as Player
        if (cmd == null) {
            launch(Dispatchers.IO) {
                AuctionGui(AstraPlayerMenuUtility(sender)).open()
            }

            return@registerCommand
        }
        when (cmd) {
            "sell" -> sell(sender, args)

        }
    }

    private fun sell(player: Player, args: Array<out String>) {
        launch {
            val auctionsAmount = Repository.countPlayerAuctions(player)
            if ((auctionsAmount ?: 0) > AstraAuctions.pluginConfig.auction.maxAuctionPerPlayer) {
                player.sendMessage(Translation.instanse.maxAuctions)
                player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                return@launch
            }
            val price = args.getArgumentString(Arguments.price)?.toFloatOrNull()
            var amount = args.getArgumentString(Arguments.amount)?.toIntOrNull()?:1
            val item = player.inventory.itemInMainHand
            amount = min(item.amount, amount)

            if (price == null) {
                player.sendMessage(Translation.instanse.wrongArgs)
                player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                return@launch
            }
            if (price > AstraAuctions.pluginConfig.auction.maxPrice || price < AstraAuctions.pluginConfig.auction.minPrice) {
                player.sendMessage(Translation.instanse.wrongPrice)
                player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                return@launch
            }

            if (item == null || item.type == Material.AIR) {
                player.sendMessage(Translation.instanse.wrongItemInHand)
                player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                return@launch
            }
            val itemClone = item.clone().apply { this.amount = amount }
            val auction = Auction(player, itemClone, price)

            Repository.insertAuction(auction, object : Callback() {
                override fun <T> onSuccess(result: T?) {
                    item.amount -= amount
                    player.sendMessage(Translation.instanse.auctionAdded)
                    player.playSound(AstraAuctions.pluginConfig.sounds.success)
                    if (AstraAuctions.pluginConfig.auction.announce)
                        Bukkit.broadcastMessage(Translation.instanse.announce.replace("%player%", player.name))
                }

                override fun onFailure(e: Exception) {
                    player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                    player.sendMessage(Translation.instanse.dbError + "${e.message}")
                }

            })
        }


    }
}



