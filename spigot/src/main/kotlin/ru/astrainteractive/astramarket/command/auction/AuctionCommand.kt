package ru.astrainteractive.astramarket.command.auction

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.command.api.Command

interface AuctionCommand : Command<AuctionCommand.Result, AuctionCommand.Result> {
    sealed interface Result {
        data object NoPermission : Result
        data object WrongUsage : Result
        data object NotPlayer : Result
        data object WrongPrice : Result
        class OpenExpired(val player: Player) : Result
        class OpenAuctions(val player: Player) : Result
        class Sell(
            val player: Player,
            val itemInstance: ItemStack,
            val amount: Int,
            val price: Float
        ) : Result
    }
}
