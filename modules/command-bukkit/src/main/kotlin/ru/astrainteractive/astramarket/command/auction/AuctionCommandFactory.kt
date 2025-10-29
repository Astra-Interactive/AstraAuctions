package ru.astrainteractive.astramarket.command.auction

import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requireArgument
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
    private fun create(alias: String): LiteralCommandNode<CommandSourceStack> {
        return command(alias) {
            literal("sell") {
                argument("price", FloatArgumentType.floatArg(0f, Float.MAX_VALUE)) { priceArg ->
                    argument("amount", IntegerArgumentType.integer(0, Int.MAX_VALUE)) { amountArg ->
                        runs(errorHandler::handle) { ctx ->
                            val player = ctx.requirePlayer()
                            AuctionCommand.Result.Sell(
                                player = player,
                                itemInstance = player
                                    .inventory
                                    .itemInMainHand,
                                amount = ctx.requireArgument(amountArg),
                                price = ctx.requireArgument(priceArg)
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
        }.build()
    }

    fun create(): List<LiteralCommandNode<CommandSourceStack>> {
        return listOf("market", "ah", "auctionhouse", "aauc", "amarket")
            .map(::create)
    }
}
