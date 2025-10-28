package ru.astrainteractive.astramarket.command.reload

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class ReloadCommandFactory(
    private val plugin: Lifecycle,
    private val errorHandler: BrigadierErrorHandler,
    translationKrate: CachedKrate<PluginTranslation>,
    kyori: CachedKrate<KyoriComponentSerializer>
) : KyoriComponentSerializer by kyori.unwrap() {
    private val translation by translationKrate

    fun create(): LiteralCommandNode<CommandSourceStack> {
        return command("reload") {
            runs(errorHandler::handle) { ctx ->
                ctx.requirePermission(PluginPermission.Reload)
                translation.general.reloadStarted
                    .let(::toComponent)
                    .run(ctx.source.sender::sendMessage)
                plugin.onReload()
                translation.general.reloadSuccess
                    .let(::toComponent)
                    .run(ctx.source.sender::sendMessage)
            }
        }.build()
    }
}
