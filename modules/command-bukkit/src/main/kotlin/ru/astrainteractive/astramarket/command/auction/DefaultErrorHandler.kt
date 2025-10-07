package ru.astrainteractive.astramarket.command.auction

import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.error.ErrorHandler
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.exception.NoPlayerException
import ru.astrainteractive.astralibs.command.api.exception.NoPotionEffectTypeException
import ru.astrainteractive.astralibs.command.api.exception.StringDescCommandException
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class DefaultErrorHandler(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) : ErrorHandler<BukkitCommandContext>,
    KyoriComponentSerializer by kyoriKrate.unwrap(),
    Logger by JUtiltLogger("AstraMarket-DefaultErrorHandler").withoutParentHandlers() {
    private val translation by pluginTranslationKrate

    override fun handle(ctx: BukkitCommandContext, throwable: Throwable) {
        when (throwable) {
            is NoPermissionException -> {
                ctx.sender.sendMessage(translation.general.noPermissions.component)
            }

            AuctionCommand.Error.NotPlayer -> {
                ctx.sender.sendMessage(translation.general.onlyForPlayers.component)
            }

            AuctionCommand.Error.WrongPrice -> {
                ctx.sender.sendMessage(translation.auction.wrongPrice.component)
            }

            AuctionCommand.Error.WrongUsage -> {
                ctx.sender.sendMessage(translation.general.wrongArgs.component)
            }

            is BadArgumentException,
            is StringDescCommandException,
            is NoPotionEffectTypeException,
            is NoPlayerException,
            is CommandException -> error { "${throwable::class} not handled" }

            else -> error { "${throwable::class} not handled" }
        }
    }
}
