package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Permissions
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.displayNameOrMaterialName
import org.bukkit.entity.Player
/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
class ExpireAuctionUseCase : UseCase<Boolean, ExpireAuctionUseCase.Params>() {
    class Params(
        val auction: Auction,
        val player: Player? = null
    )
    override suspend fun run(params: Params): Boolean {
        val player = params.player
        val _auction = params.auction
        if (player != null && !player.hasPermission(Permissions.expire)) {
            player.sendMessage(Translation.noPermissions)
            return false
        }
        Logger.log("Player ${player?.name} forced auction to expire ${_auction}", Repository.TAG, consolePrint = false)
        val auction = Repository.fetchAuction(_auction.id)?.firstOrNull() ?: return false
        auction.owner?.player?.sendMessage(
            Translation.notifyAuctionExpired
                .replace("%item%", auction.itemStack.displayNameOrMaterialName())
                .replace("%price%", auction.price.toString())
        )
        val result = Repository.expireAuction(auction)
        if (result == null) {
            player?.sendMessage(Translation.unexpectedError)
        } else
            player?.sendMessage(Translation.auctionHasBeenExpired)
        return (result != null)
    }
}