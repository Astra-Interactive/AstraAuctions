package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.sideeffect.BukkitCommandSideEffect
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies

class AuctionCommandSideEffect(
    private val dependencies: AuctionCommandDependencies
) : BukkitCommandSideEffect<AuctionCommand.Result>,
    AuctionCommandDependencies by dependencies {
    override fun handle(commandContext: BukkitCommandContext, result: AuctionCommand.Result) {
        when (result) {
            AuctionCommand.Result.NoPermission -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.noPermissions.let(::toComponent))
            }

            AuctionCommand.Result.NotPlayer -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.onlyForPlayers.let(::toComponent))
            }

            AuctionCommand.Result.WrongPrice -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.auction.wrongPrice.let(::toComponent))
            }

            AuctionCommand.Result.WrongUsage -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.wrongArgs.let(::toComponent))
            }

            is AuctionCommand.Result.OpenExpired,
            is AuctionCommand.Result.Sell,
            is AuctionCommand.Result.OpenAuctions -> Unit
        }
    }
}
