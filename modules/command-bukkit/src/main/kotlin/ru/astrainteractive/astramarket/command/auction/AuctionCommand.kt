package ru.astrainteractive.astramarket.command.auction

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.command.api.exception.CommandException
import java.util.UUID

internal interface AuctionCommand {
    sealed interface Result {
        class OpenSlots(
            val player: Player,
            val isExpired: Boolean,
            val targetPlayerUUID: UUID?
        ) : Result

        class OpenPlayers(val player: Player, val isExpired: Boolean) : Result
        class Sell(
            val player: Player,
            val itemInstance: ItemStack,
            val amount: Int,
            val price: Float
        ) : Result
    }

    sealed class Error(message: String) : CommandException(message) {
        data object WrongUsage : Error("Wrong usage")
        data object NotPlayer : Error("Not player")
        data object WrongPrice : Error("Wrong price")
    }
}
