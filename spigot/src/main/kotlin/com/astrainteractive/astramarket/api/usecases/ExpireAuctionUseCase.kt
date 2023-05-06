package com.astrainteractive.astramarket.api.usecases

import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.plugin.PluginPermission
import com.astrainteractive.astramarket.utils.displayNameOrMaterialName
import com.astrainteractive.astramarket.utils.itemStack
import com.astrainteractive.astramarket.utils.owner
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astralibs.getValue

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
class ExpireAuctionUseCase : UseCase<Boolean, ExpireAuctionUseCase.Params> {
    private val dataSource by RootModuleImpl.auctionsApi
    private val translation by RootModuleImpl.translation
    class Params(
        val auction: AuctionDTO,
        val player: Player? = null
    )
    override suspend fun run(params: Params): Boolean {
        val player = params.player
        val receivedAuction = params.auction
        if (player != null && !PluginPermission.Expire.hasPermission(player)) {
            player.sendMessage(translation.noPermissions)
            return false
        }
        val auction = dataSource.fetchAuction(receivedAuction.id) ?: return false
        auction.owner?.player?.sendMessage(
            translation.notifyAuctionExpired
                .replace("%item%", auction.itemStack.displayNameOrMaterialName())
                .replace("%price%", auction.price.toString())
        )
        val result = dataSource.expireAuction(auction)
        if (result == null) {
            player?.sendMessage(translation.unexpectedError)
        } else {
            player?.sendMessage(translation.auctionHasBeenExpired)
        }
        return (result != null)
    }
}
