package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.command.Command
import ru.astrainteractive.astralibs.command.api.commandfactory.BukkitCommandFactory
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistry
import ru.astrainteractive.astralibs.command.api.registry.BukkitCommandRegistryContext.Companion.toCommandRegistryContext
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies

class AuctionCommandRegistry(
    private val dependencies: AuctionCommandDependencies
) {
    fun register() {
        val command = BukkitCommandFactory.create(
            alias = "amarket",
            commandParser = AuctionCommandParser(),
            commandExecutor = AuctionCommandExecutor(dependencies),
            commandSideEffect = AuctionCommandSideEffect(dependencies),
            mapper = Command.Mapper.NoOp()
        )
        BukkitCommandRegistry.register(
            command = command,
            registryContext = dependencies.plugin.toCommandRegistryContext()
        )
    }
}
