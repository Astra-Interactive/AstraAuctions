package ru.astrainteractive.astramarket.command.auction

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import kotlin.math.max
import kotlin.math.min

internal class AuctionCommandExecutor(
    private val dependencies: AuctionCommandDependencies
) : CommandExecutor<AuctionCommand.Result>, AuctionCommandDependencies by dependencies {
    override fun execute(input: AuctionCommand.Result) {
        when (input) {
            is AuctionCommand.Result.OpenSlots -> {
                val route = GuiRouter.Route.Slots(
                    player = input.player,
                    isExpired = input.isExpired,
                    targetPlayerUUID = input.targetPlayerUUID
                )
                router.navigate(route)
            }

            is AuctionCommand.Result.OpenPlayers -> {
                val route = GuiRouter.Route.Players(
                    player = input.player,
                    isExpired = input.isExpired
                )
                router.navigate(route)
            }

            is AuctionCommand.Result.Sell -> {
                val itemInstance = input.itemInstance
                val clonedItem = itemInstance.clone().apply {
                    this.amount = max(min(itemInstance.amount, input.amount), 1)
                }
                val encodedItem = itemStackEncoder.toByteArray(clonedItem)
                scope.launch(dispatchers.IO) {
                    val marketSlot = MarketSlot(
                        id = -1,
                        minecraftUuid = input.player.uniqueId.toString(),
                        time = System.currentTimeMillis(),
                        item = encodedItem,
                        price = input.price,
                        expired = false
                    )
                    val param = CreateAuctionUseCase.Params(
                        marketSlot = marketSlot,
                        playerUUID = input.player.uniqueId,
                    )
                    val useCaseResult = createAuctionUseCase.invoke(param)
                    withContext(dispatchers.Main) {
                        if (useCaseResult) itemInstance.amount -= input.amount
                    }
                }
            }

            AuctionCommand.Result.WrongUsage,
            AuctionCommand.Result.NoPermission,
            AuctionCommand.Result.NotPlayer,
            AuctionCommand.Result.WrongPrice -> Unit
        }
    }
}
