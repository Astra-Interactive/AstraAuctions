package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astramarket.command.di.CommandContainer

class CommandManager(module: CommandContainer) : CommandContainer by module {

    init {
        reloadCommand()
        amarketCommand()
        tabCompleter()
    }
}
