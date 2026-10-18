package ru.astrainteractive.astramarket.command.auction.command

import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

internal sealed interface AuctionCommand {
    class OpenSlots(
        val player: OnlineKPlayer,
        val isExpired: Boolean,
        val targetPlayerUUID: UUID?
    ) : AuctionCommand

    class OpenPlayers(
        val player: OnlineKPlayer,
        val isExpired: Boolean
    ) : AuctionCommand

    class Sell(
        val player: OnlineKPlayer,
        val itemInstance: ItemStack,
        val amount: Int,
        val price: Float
    ) : AuctionCommand
}
