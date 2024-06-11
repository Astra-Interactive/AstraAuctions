package ru.astrainteractive.astramarket.command.auction

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.parser.BukkitCommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.core.PluginPermission

internal class AuctionCommandParser : BukkitCommandParser<AuctionCommand.Result> {
    override fun parse(commandContext: BukkitCommandContext): AuctionCommand.Result {
        if (!commandContext.sender.toPermissible().hasPermission(PluginPermission.Amarket)) {
            return AuctionCommand.Result.NoPermission
        }
        return when (commandContext.args.getOrNull(0)) {
            "sell" -> {
                val price = commandContext.args.getOrNull(1)?.toFloatOrNull()
                price ?: return AuctionCommand.Result.WrongPrice
                val amount = commandContext.args.getOrNull(2)?.toIntOrNull() ?: 1
                val player = commandContext.sender as? Player
                player ?: return AuctionCommand.Result.NotPlayer
                val itemInstance = player.inventory.itemInMainHand
                AuctionCommand.Result.WrongPrice
                AuctionCommand.Result.Sell(
                    player = player,
                    itemInstance = itemInstance,
                    amount = amount,
                    price = price
                )
            }

            "players" -> {
                val player = commandContext.sender as? Player
                player ?: return AuctionCommand.Result.NotPlayer
                AuctionCommand.Result.OpenPlayers(
                    player = player,
                    isExpired = false
                )
            }

            // Open and else
            else -> {
                val player = commandContext.sender as? Player
                player ?: return AuctionCommand.Result.NotPlayer
                val targetPlayerUuid = commandContext.args
                    .getOrNull(1)
                    ?.let(Bukkit::getPlayer)
                    ?.uniqueId

                AuctionCommand.Result.OpenSlots(
                    player = player,
                    isExpired = commandContext.args.getOrNull(0) == "expired",
                    targetPlayerUUID = targetPlayerUuid
                )
            }
        }
    }
}
