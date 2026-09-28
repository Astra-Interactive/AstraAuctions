package ru.astrainteractive.astramarket.core.command

import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.ArgumentConverterException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import ru.astrainteractive.astralibs.command.api.exception.LocalizableComponentCommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.NoPotionEffectTypeException
import ru.astrainteractive.astralibs.command.api.exception.NotPlayerExecutorException
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class CommandExceptionHandler(
    private val multiplatformCommand: MultiplatformCommand,
    translationKrate: CachedKrate<PluginTranslation>
) : Logger by JUtiltLogger("AstraMarket-CommandExceptionHandler") {
    private val translation by translationKrate

    private fun messageOf(throwable: Throwable, commandName: String): LocalizableComponent {
        return when (throwable) {
            is LocalizableComponentCommandException -> throwable.localizableComponent
            is NoPermissionException -> translation.error.noPermission
            is NotPlayerExecutorException -> translation.error.onlyPlayerCommand
            is NoPlayerException -> translation.error.playerNotFound
            is ArgumentConverterException,
            is BadArgumentException,
            is NoPotionEffectTypeException -> translation.error.invalidArgument

            is CommandException -> translation.error.wrongUsage
            else -> {
                error(throwable) { "#messageOf /$commandName failed with an unexpected exception" }
                translation.error.unexpected
            }
        }
    }

    fun handle(ctx: CommandContext<Any>, throwable: Throwable) {
        val commandName = ctx.input.substringBefore(' ')
        val sender = runCatching { with(multiplatformCommand) { ctx.getSender() } }
            .getOrElse { senderError ->
                error(throwable) {
                    "#handle /$commandName failed and its sender could not be resolved: ${senderError.message}"
                }
                return
            }
        sender.sendMessage(messageOf(throwable, commandName))
    }
}
