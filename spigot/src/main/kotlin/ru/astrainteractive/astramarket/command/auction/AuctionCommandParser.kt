package ru.astrainteractive.astramarket.command.auction

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astramarket.plugin.PluginPermission

class AuctionCommandParser : CommandParser<AuctionCommand.Result> {
    override fun parse(args: Array<out String>, sender: CommandSender): AuctionCommand.Result {
        if (!sender.toPermissible().hasPermission(PluginPermission.Amarket)) {
            return AuctionCommand.Result.NoPermission
        }
        return when (args.getOrNull(0)) {
            "sell" -> {
                val price = args.getOrNull(1)?.toFloatOrNull()
                price ?: return AuctionCommand.Result.WrongPrice
                val amount = args.getOrNull(2)?.toIntOrNull() ?: 1
                val player = sender as? Player
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

            "open" -> {
                val player = sender as? Player
                player ?: return AuctionCommand.Result.NotPlayer
                AuctionCommand.Result.OpenAuctions(player)
            }

            "expired" -> {
                val player = sender as? Player
                player ?: return AuctionCommand.Result.NotPlayer
                AuctionCommand.Result.OpenExpired(player)
            }

            else -> AuctionCommand.Result.WrongUsage
        }
    }
}
