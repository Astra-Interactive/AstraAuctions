package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astramarket.command.di.CommandContainer

class CommandManager(
    module: CommandContainer
) : CommandContainer by module,
    BukkitTranslationContext by module.translationContext {

    init {
        auctionCommandFactory.create()
        reloadCommand()
        tabCompleter()
    }
}
