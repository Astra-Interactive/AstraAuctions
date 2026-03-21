package ru.astrainteractive.astramarket.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.server.KAudience
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

class ReloadCommandFactory(
    private val plugin: Lifecycle,
    private val errorHandler: BrigadierErrorHandler,
    translationKrate: CachedKrate<PluginTranslation>,
    kyori: CachedKrate<KyoriComponentSerializer>,
    private val multiplatformCommand: MultiplatformCommand<*>,
) : KyoriComponentSerializer by kyori.unwrap() {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<*> {
        return with(multiplatformCommand) {
            command("amarketreload") {
                runs(errorHandler::handle) { ctx ->
                    ctx.requirePermission(PluginPermission.Reload)
                    ctx.getSender()
                        .tryCast<KAudience>()
                        ?.sendMessage(translation.general.reloadStarted.component)
                    plugin.onReload()
                    ctx.getSender()
                        .tryCast<KAudience>()
                        ?.sendMessage(translation.general.reloadSuccess.component)
                }
            }
        }
    }
}
