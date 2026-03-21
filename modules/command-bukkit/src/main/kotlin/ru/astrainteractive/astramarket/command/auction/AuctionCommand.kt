package ru.astrainteractive.astramarket.command.auction

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

internal interface AuctionCommand {
    sealed interface Result {
        class OpenSlots(
            val player: OnlineKPlayer,
            val isExpired: Boolean,
            val targetPlayerUUID: UUID?
        ) : Result

        class OpenPlayers(
            val player: OnlineKPlayer,
            val isExpired: Boolean
        ) : Result

        class Sell(
            val player: OnlineKPlayer,
            val itemInstance: ItemStack,
            val amount: Int,
            val price: Float
        ) : Result
    }
}
