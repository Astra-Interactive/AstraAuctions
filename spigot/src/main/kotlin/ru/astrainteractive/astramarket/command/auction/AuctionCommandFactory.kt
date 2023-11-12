package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.klibs.kdi.Factory

class AuctionCommandFactory(
    private val dependencies: AuctionCommandDependencies
) : Factory<AuctionCommand> {
    private inner class AuctionCommandImpl :
        AuctionCommand,
        Command<AuctionCommand.Result, AuctionCommand.Result> by DefaultCommandFactory.create(
            alias = "amarket",
            commandParser = AuctionCommandParser(),
            commandExecutor = AuctionCommandExecutor(dependencies),
            resultHandler = AuctionCommandResultHandler(dependencies),
            mapper = Command.Mapper.NoOp()
        )

    override fun create(): AuctionCommand {
        return AuctionCommandImpl().also {
            it.register(dependencies.plugin)
        }
    }
}
