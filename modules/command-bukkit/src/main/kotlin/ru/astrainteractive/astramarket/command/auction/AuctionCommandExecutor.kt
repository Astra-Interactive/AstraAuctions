package ru.astrainteractive.astramarket.command.auction

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.core.CoroutineExt.launchWithLock
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import kotlin.math.max
import kotlin.math.min

internal class AuctionCommandExecutor(
    private val dependencies: AuctionCommandDependencies
) : CommandExecutor<AuctionCommand.Result>,
    AuctionCommandDependencies by dependencies,
    Logger by JUtiltLogger("AuctionCommandExecutor") {
    private val mutex = Mutex()

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

            is AuctionCommand.Result.Sell -> scope.launchWithLock(mutex, limitedIoDispatcher) {
                val itemInstance = input.itemInstance
                val calculatedAmount = max(min(itemInstance.amount, input.amount), 1)
                val clonedItem = itemInstance.clone().apply {
                    amount = calculatedAmount
                }
                val encodedItem = itemStackEncoder.toByteArray(clonedItem)

                info {
                    buildString {
                        append("User ${input.player.name} trying to sell ${input.amount}")
                        append("Calculated amount $calculatedAmount of $itemInstance")
                    }
                }

                if (itemInstance.amount <= 0) return@launchWithLock
                withContext(dispatchers.Main) { itemInstance.amount -= calculatedAmount }
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
                    if (!useCaseResult) itemInstance.amount += calculatedAmount
                }
            }
        }
    }
}
