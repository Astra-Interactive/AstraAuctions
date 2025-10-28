package ru.astrainteractive.astramarket.command.auction

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

internal class AuctionCommandFactory(
    kyori: CachedKrate<KyoriComponentSerializer>,
    private val executor: AuctionCommandExecutor,
    private val errorHandler: BrigadierErrorHandler
) : KyoriComponentSerializer by kyori.unwrap() {

    @Suppress("LongMethod")
    fun create(): LiteralCommandNode<CommandSourceStack> {
        return command("amarket") {
            literal("sell") {
                argument("price", IntegerArgumentType.integer(0, Int.MAX_VALUE)) {
                    argument("amount", IntegerArgumentType.integer(0, Int.MAX_VALUE)) {
                        runs(errorHandler::handle) { ctx ->
                            val player = ctx.requirePlayer()
                            AuctionCommand.Result.Sell(
                                player = player,
                                itemInstance = player
                                    .inventory
                                    .itemInMainHand,
                                amount = ctx
                                    .getArgument("amount", Int::class.java),
                                price = ctx
                                    .getArgument("price", Int::class.java)
                                    .toFloat()
                            ).run(executor::execute)
                        }
                    }
                    runs(errorHandler::handle) { ctx ->
                        val player = ctx.requirePlayer()
                        AuctionCommand.Result.Sell(
                            player = player,
                            itemInstance = player
                                .inventory
                                .itemInMainHand,
                            amount = 1,
                            price = ctx
                                .getArgument("price", Int::class.java)
                                .toFloat()
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
        }.build()
    }
}
