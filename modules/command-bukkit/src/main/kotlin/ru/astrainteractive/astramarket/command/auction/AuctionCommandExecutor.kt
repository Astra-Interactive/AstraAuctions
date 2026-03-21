package ru.astrainteractive.astramarket.command.auction

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.klibs.mikro.core.coroutines.launch
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import kotlin.math.max
import kotlin.math.min

internal class AuctionCommandExecutor(
    private val router: GuiRouter,
    private val dispatchers: KotlinDispatchers,
    private val ioScope: CoroutineScope,
    private val itemStackEncoder: ItemStackEncoder,
    private val createAuctionUseCase: CreateAuctionUseCase
) : Logger by JUtiltLogger("AstraMarket-AuctionCommandExecutor").withoutParentHandlers() {
    private val mutex = Mutex()
    private val limitedIoDispatcher = dispatchers.IO.limitedParallelism(1)

    fun execute(input: AuctionCommand.Result) {
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

            is AuctionCommand.Result.Sell -> ioScope.launch(mutex, limitedIoDispatcher) {
                val itemInstance = input.itemInstance
                val calculatedAmount = max(min(itemInstance.amount, input.amount), 1)
                val clonedItem = itemInstance.clone().apply {
                    amount = calculatedAmount
                }
                val encodedItem = itemStackEncoder.toByteArray(clonedItem)
                    .onFailure { error { "#execute could not deserialize item" } }
                    .getOrNull()
                    ?: return@launch

                info {
                    buildString {
                        append("User ${input.player.name} trying to sell ${input.amount}")
                        append("Calculated amount $calculatedAmount of $itemInstance")
                    }
                }

                if (itemInstance.amount <= 0) return@launch
                withContext(dispatchers.Main) { itemInstance.amount -= calculatedAmount }
                val marketSlot = MarketSlot(
                    id = -1,
                    minecraftUuid = input.player.uuid.toString(),
                    time = System.currentTimeMillis(),
                    item = encodedItem,
                    price = input.price,
                    expired = false,
                    minecraftUsername = input.player.name
                )
                val param = CreateAuctionUseCase.Params(
                    marketSlot = marketSlot,
                    playerUUID = input.player.uuid,
                )
                val useCaseResult = createAuctionUseCase.invoke(param)
                withContext(dispatchers.Main) {
                    if (!useCaseResult) itemInstance.amount += calculatedAmount
                }
            }
        }
    }
}
