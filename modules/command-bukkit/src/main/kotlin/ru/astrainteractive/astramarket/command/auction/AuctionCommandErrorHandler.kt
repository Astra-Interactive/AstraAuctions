package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies

internal class AuctionCommandErrorHandler(
    private val dependencies: AuctionCommandDependencies
) : ErrorHandler<BukkitCommandContext>,
    AuctionCommandDependencies by dependencies {
    override fun handle(commandContext: BukkitCommandContext, throwable: Throwable) {
        when (throwable) {
            is NoPermissionException -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.noPermissions.component)
            }

            AuctionCommand.Error.NotPlayer -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.onlyForPlayers.component)
            }

            AuctionCommand.Error.WrongPrice -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.auction.wrongPrice.component)
            }

            AuctionCommand.Error.WrongUsage -> with(kyoriComponentSerializer) {
                commandContext.sender.sendMessage(translation.general.wrongArgs.component)
            }
        }
    }
}
