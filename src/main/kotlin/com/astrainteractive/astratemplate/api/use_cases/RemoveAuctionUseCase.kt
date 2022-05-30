package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class RemoveAuctionUseCase : UseCase<Boolean, RemoveAuctionUseCase.Params>() {
    class Params(
        val auction: Auction,
        val player: Player
    ) {
        operator fun component1() = auction
        operator fun component2() = player
    }

    override suspend fun run(params: Params): Boolean {
        val (_auction, player) = params

        val auction = Repository.fetchAuction(_auction.id)?.firstOrNull() ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            player.sendMessage(Translation.notAuctionOwner)
            return false
        }
        Logger.log("Player ${player.name} removed his auction ${auction}", Repository.TAG, consolePrint = false)
        val item = auction.itemStack
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.inventoryFull)
            return false
        }
        val result = Repository.deleteAuction(auction.id)
        return if (result != null) {
            player.sendMessage(Translation.auctionDeleted)
            player.inventory.addItem(item)
            true
        } else {
            player.sendMessage(Translation.unexpectedError)
            false
        }
    }
}