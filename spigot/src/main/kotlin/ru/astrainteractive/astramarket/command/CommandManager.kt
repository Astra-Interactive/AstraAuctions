package ru.astrainteractive.astramarket.command

import ru.astrainteractive.astramarket.command.di.CommandsModule

class CommandManager(module: CommandsModule) : CommandsModule by module {

    init {
        reloadCommand()
        amarketCommand()
        tabCompleter()
    }
}
