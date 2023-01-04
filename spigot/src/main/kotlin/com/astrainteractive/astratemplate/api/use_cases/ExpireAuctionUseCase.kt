package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astratemplate.modules.DataSourceModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.*
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.domain.IUseCase

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
class ExpireAuctionUseCase : IUseCase<Boolean, ExpireAuctionUseCase.Params> {
    private val dataSource by DataSourceModule
    private val translation by TranslationModule
    class Params(
        val auction: AuctionDTO,
        val player: Player? = null
    )
    override suspend fun run(params: Params): Boolean {
        val player = params.player
        val _auction = params.auction
        if (player != null && !player.hasPermission(Permissions.expire)) {
            player.sendMessage(translation.noPermissions)
            return false
        }
        val auction = dataSource.fetchAuction(_auction.id) ?: return false
        auction.owner?.player?.sendMessage(
            translation.notifyAuctionExpired
                .replace("%item%", auction.itemStack.displayNameOrMaterialName())
                .replace("%price%", auction.price.toString())
        )
        val result = dataSource.expireAuction(auction)
        if (result == null) {
            player?.sendMessage(translation.unexpectedError)
        } else
            player?.sendMessage(translation.auctionHasBeenExpired)
        return (result != null)
    }
}