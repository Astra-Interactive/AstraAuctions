package ru.astrainteractive.astramarket.command.auction

import org.bukkit.command.CommandSender
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies

class AuctionCommandResultHandler(
    private val dependencies: AuctionCommandDependencies
) : Command.ResultHandler<AuctionCommand.Result>,
    AuctionCommandDependencies by dependencies {
    override fun handle(commandSender: CommandSender, result: AuctionCommand.Result) {
        when (result) {
            AuctionCommand.Result.NoPermission -> with(kyoriComponentSerializer) {
                commandSender.sendMessage(translation.general.noPermissions.let(::toComponent))
            }

            AuctionCommand.Result.NotPlayer -> with(kyoriComponentSerializer) {
                commandSender.sendMessage(translation.general.onlyForPlayers.let(::toComponent))
            }

            AuctionCommand.Result.WrongPrice -> with(kyoriComponentSerializer) {
                commandSender.sendMessage(translation.auction.wrongPrice.let(::toComponent))
            }

            AuctionCommand.Result.WrongUsage -> with(kyoriComponentSerializer) {
                commandSender.sendMessage(translation.general.wrongArgs.let(::toComponent))
            }

            is AuctionCommand.Result.OpenExpired,
            is AuctionCommand.Result.Sell,
            is AuctionCommand.Result.OpenAuctions -> Unit
        }
    }
}
