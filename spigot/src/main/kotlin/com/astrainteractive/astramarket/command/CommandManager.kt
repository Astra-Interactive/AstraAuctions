package com.astrainteractive.astramarket.command

import com.astrainteractive.astramarket.command.di.CommandsModule

class CommandManager(module: CommandsModule) : CommandsModule by module {

    init {
        reloadCommand()
        amarketCommand()
        tabCompleter()
    }
}
