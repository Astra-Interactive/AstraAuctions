package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.util.PluginExt.setCommandExecutor
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies

internal class AuctionCommandRegistry(
    private val dependencies: AuctionCommandDependencies
) {
    fun register() {
        dependencies.plugin.setCommandExecutor(
            alias = "amarket",
            commandParser = AuctionCommandParser(),
            commandExecutor = AuctionCommandExecutor(dependencies),
            errorHandler = AuctionCommandErrorHandler(dependencies)
        )
    }
}
