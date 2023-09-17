package com.astrainteractive.astramarket.command

import com.astrainteractive.astramarket.gui.domain.usecases.CreateAuctionUseCase
import com.astrainteractive.astramarket.plugin.PluginPermission
import com.astrainteractive.astramarket.util.playSound
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.Command
import ru.astrainteractive.astralibs.command.registerCommand

fun CommandManager.amarketCommand() = plugin.registerCommand("amarket") {
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
            val menu = guiModule.auctionGuiFactory(sender, false).create()
            withContext(dispatchers.BukkitMain) { menu.open() }
        }

        "open", null -> scope.launch(dispatchers.IO) {
            val menu = guiModule.auctionGuiFactory(sender, false).create()
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}

private val CommandManager.sellCommand: Command.() -> Unit
    get() = command@{
        if (!PluginPermission.Sell.hasPermission(sender)) return@command
        val player = sender as? Player ?: return@command
        val maxAuctionsAllowed =
            PluginPermission.SellMax.maxPermissionSize(player) ?: configuration.auction.maxAuctionPerPlayer
        val price = argument(1) {
            it?.toFloatOrNull()
        }.onFailure {
            player.sendMessage(translation.wrongArgs)
            player.playSound(configuration.sounds.fail)
        }.successOrNull()?.value ?: return@command

        val amount = argument(2) {
            it?.toIntOrNull() ?: 1
        }.onFailure {
        }.successOrNull()?.value ?: return@command
        val item = player.inventory.itemInMainHand

        scope.launch(dispatchers.IO) {
            useCasesModule.createAuctionUseCase.invoke(
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
