package ru.astrainteractive.astramarket.command.errorhandler

import com.mojang.brigadier.context.CommandContext
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class BrigadierErrorHandler(
    kyoriComponentSerializer: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
    private val multiplatformCommand: MultiplatformCommand,
) : Logger by JUtiltLogger("AstraMarket-ErrorHandler").withoutParentHandlers(),
    KyoriComponentSerializer by kyoriComponentSerializer.unwrap() {
    private val translation by translationKrate

    fun handle(ctx: CommandContext<Any>, throwable: Throwable) {
        with(multiplatformCommand) {
            when (throwable) {
                is NoPermissionException -> {
                    ctx.getSender().sendMessage(translation.general.noPermissions.component)
                }

                else -> {
                    error(throwable) { "Unhandled exception: ${throwable.message}" }
                }
            }
        }
    }
}
