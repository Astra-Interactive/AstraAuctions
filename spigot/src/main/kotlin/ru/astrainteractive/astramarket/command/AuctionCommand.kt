package ru.astrainteractive.astramarket.command

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.Command
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astramarket.gui.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astramarket.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.util.playSound

fun CommandManager.amarketCommand() = plugin.registerCommand("amarket") {
    val sender = this.sender
    if (sender !is Player) {
        stringSerializer.sendMessage(translation.general.onlyForPlayers, sender)
        return@registerCommand
    }

    if (!permissionManager.hasPermission(sender.uniqueId, PluginPermission.Amarket)) {
        stringSerializer.sendMessage(translation.general.noPermissions, sender)
        return@registerCommand
    }
    when (args.getOrNull(0)) {
        "sell" -> sellCommand.invoke(this)
        "expired" -> scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }

        "open", null -> scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}

private val CommandManager.sellCommand: Command.() -> Unit
    get() = command@{
        val player = sender as? Player ?: return@command
        if (!permissionManager.hasPermission(player.uniqueId, PluginPermission.Sell)) return@command

        val maxAuctionsAllowed = permissionManager.maxPermissionSize(
            player.uniqueId,
            PluginPermission.SellMax
        ) ?: configuration.auction.maxAuctionPerPlayer
        val price = argument(1) {
            it?.toFloatOrNull()
        }.onFailure {
            stringSerializer.sendMessage(translation.general.wrongArgs, player)
            player.playSound(configuration.sounds.fail)
        }.successOrNull()?.value ?: return@command

        val amount = argument(2) {
            it?.toIntOrNull() ?: 1
        }.onFailure {
        }.successOrNull()?.value ?: return@command
        val item = player.inventory.itemInMainHand

        scope.launch(dispatchers.IO) {
            createAuctionUseCase.invoke(
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
