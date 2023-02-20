package com.astrainteractive.astramarket.commands

import com.astrainteractive.astramarket.api.use_cases.CreateAuctionUseCase
import com.astrainteractive.astramarket.gui.auctions.AuctionGui
import com.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.plugin.PluginPermission
import com.astrainteractive.astramarket.utils.playSound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.commands.Command
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.di.getValue

class AuctionCommand {
    private val translation by Modules.translation
    private val config by Modules.configuration

    val amarket = AstraLibs.instance.registerCommand("amarket") {
        val sender = this.sender
        if (sender !is Player) {
            sender.sendMessage(translation.onlyForPlayers)
            return@registerCommand
        }
        if (!PluginPermission.Amarket.hasPermission(sender)) {
            sender.sendMessage(translation.noPermissions)
            return@registerCommand
        }
        when (args.getOrNull(0)) {
            "sell" -> sellCommand.invoke(this)
            "expired" -> PluginScope.launch(Dispatchers.IO) {
                ExpiredAuctionGui(sender).open()
            }

            "open", null -> PluginScope.launch(Dispatchers.IO) {
                AuctionGui(sender).open()
            }

        }
    }

    private val sellCommand: Command.() -> Unit = command@{
        if (!PluginPermission.Sell.hasPermission(sender)) return@command
        val player = sender as? Player ?: return@command
        val maxAuctionsAllowed = PluginPermission.SellMax.permissionSize(player) ?: config.auction.maxAuctionPerPlayer ?: 1

        val price = argument(1) {
            it?.toFloatOrNull()
        }.onFailure {
            player.sendMessage(translation.wrongArgs)
            player.playSound(config.sounds.fail)
        }.successOrNull()?.value ?: return@command

        val amount = argument(2) {
            it?.toIntOrNull() ?: 1
        }.onFailure {

        }.successOrNull()?.value ?: return@command
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



