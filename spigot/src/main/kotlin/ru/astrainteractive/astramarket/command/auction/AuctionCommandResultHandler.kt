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
            AuctionCommand.Result.NoPermission -> with(translationContext) {
                commandSender.sendMessage(translation.general.noPermissions)
            }

            AuctionCommand.Result.NotPlayer -> with(translationContext) {
                commandSender.sendMessage(translation.general.onlyForPlayers)
            }

            AuctionCommand.Result.WrongPrice -> with(translationContext) {
                commandSender.sendMessage(translation.auction.wrongPrice)
            }

            AuctionCommand.Result.WrongUsage -> with(translationContext) {
                commandSender.sendMessage(translation.general.wrongArgs)
            }

            is AuctionCommand.Result.OpenExpired,
            is AuctionCommand.Result.Sell,
            is AuctionCommand.Result.OpenAuctions -> Unit
        }
    }
}
