package ru.astrainteractive.astramarket.command.reload

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.command.CommandExceptionHandler
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

class ReloadLiteralArgumentBuilder(
    private val lifecyclePlugin: Lifecycle,
    private val commandExceptionHandler: CommandExceptionHandler,
    translationKrate: CachedKrate<PluginTranslation>,
    private val multiplatformCommand: MultiplatformCommand,
) {
    private val translation by translationKrate

    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("amarketreload") {
                runs(commandExceptionHandler::handle) { ctx ->
                    ctx.requirePermission(PluginPermission.Reload)
                    ctx.getSender().sendMessage(translation.reload.started)
                    lifecyclePlugin.onReload()
                    ctx.getSender().sendMessage(translation.reload.completed)
                }
            }
        }
    }
}
