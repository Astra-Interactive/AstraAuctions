package ru.astrainteractive.astramarket.command.auction

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.command.api.command.BukkitCommand
import java.util.UUID

internal interface AuctionCommand : BukkitCommand {
    sealed interface Result {
        data object NoPermission : Result
        data object WrongUsage : Result
        data object NotPlayer : Result
        data object WrongPrice : Result
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
}
