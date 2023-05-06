package com.astrainteractive.astramarket.commands

import com.astrainteractive.astramarket.api.usecases.CreateAuctionUseCase
import com.astrainteractive.astramarket.commands.di.CommandsModule
import com.astrainteractive.astramarket.gui.auctions.AuctionGui
import com.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import com.astrainteractive.astramarket.plugin.PluginPermission
import com.astrainteractive.astramarket.utils.openSync
import com.astrainteractive.astramarket.utils.playSound
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.commands.Command
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.getValue

class AuctionCommand(
    plugin: JavaPlugin,
    module: CommandsModule
) {
    private val translation by module.translation
    private val config by module.configuration
    private val scope by module.scope
    private val dispatchers by module.dispatchers

    val amarket = plugin.registerCommand("amarket") {
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
            "expired" -> scope.launch(dispatchers.IO) {
                ExpiredAuctionGui(sender).openSync()
            }

            "open", null -> scope.launch(dispatchers.IO) {
                AuctionGui(sender).openSync()
            }
        }
    }

    private val sellCommand: Command.() -> Unit = command@{
        if (!PluginPermission.Sell.hasPermission(sender)) return@command
        val player = sender as? Player ?: return@command
        val maxAuctionsAllowed = PluginPermission.SellMax.permissionSize(player) ?: config.auction.maxAuctionPerPlayer

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

        scope.launch(dispatchers.IO) {
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
