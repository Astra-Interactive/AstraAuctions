package ru.astrainteractive.astramarket.command.auction

import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class AuctionCommandFactory(
    private val executor: AuctionCommandExecutor,
    private val errorHandler: BrigadierErrorHandler,
    private val multiplatformCommand: MultiplatformCommand,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
) : KyoriComponentSerializer by kyoriKrate.unwrap() {

    @Suppress("LongMethod")
    private fun create(alias: String): LiteralArgumentBuilder<*> {
        return with(multiplatformCommand) {
            command(alias) {
                literal("sell") {
                    argument("price", FloatArgumentType.floatArg(0f, Float.MAX_VALUE)) { priceArg ->
                        argument("amount", IntegerArgumentType.integer(0, Int.MAX_VALUE)) { amountArg ->
                            runs(errorHandler::handle) { ctx ->
                                val player = ctx.requirePlayer()
                                    .tryCast<BukkitOnlineKPlayer>()
                                    ?: error("Could not get bukkit player ")
                                AuctionCommand.Result.Sell(
                                    player = player,
                                    itemInstance = player
                                        .instance
                                        .inventory
                                        .itemInMainHand,
                                    amount = ctx.requireArgument(amountArg),
                                    price = ctx.requireArgument(priceArg)
                                ).run(executor::execute)
                            }
                        }
                        runs(errorHandler::handle) { ctx ->
                            val player = ctx.requirePlayer()
                                .tryCast<BukkitOnlineKPlayer>()
                                ?: error("Could not get bukkit player ")
                            AuctionCommand.Result.Sell(
                                player = player,
                                itemInstance = player
                                    .instance
                                    .inventory
                                    .itemInMainHand,
                                amount = 1,
                                price = ctx.requireArgument(priceArg)
                            ).run(executor::execute)
                        }
                    }
                }
                literal("players") {
                    runs(errorHandler::handle) { ctx ->
                        val player = ctx.requirePlayer()
                        AuctionCommand.Result.OpenPlayers(
                            player = player,
                            isExpired = false
                        ).run(executor::execute)
                    }
                }
                runs(errorHandler::handle) { ctx ->
                    val player = ctx.requirePlayer()
                    AuctionCommand.Result.OpenSlots(
                        player = player,
                        isExpired = false,
                        targetPlayerUUID = null
                    ).run(executor::execute)
                }
            }
        }
    }

    fun create(): List<LiteralArgumentBuilder<*>> {
        return listOf("market", "ah", "auctionhouse", "aauc", "amarket")
            .map(::create)
    }
}
