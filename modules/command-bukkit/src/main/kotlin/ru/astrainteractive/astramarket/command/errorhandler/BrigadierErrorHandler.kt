package ru.astrainteractive.astramarket.command.errorhandler

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class BrigadierErrorHandler : Logger by JUtiltLogger("AstraMarket-ErrorHandler").withoutParentHandlers() {
    @Suppress("UnusedParameter")
    fun handle(ctx: CommandContext<CommandSourceStack>, throwable: Throwable) {
        when (throwable) {
            else -> {
                error(throwable) { "Unhandled exception: ${throwable.message}" }
            }
        }
    }
}
