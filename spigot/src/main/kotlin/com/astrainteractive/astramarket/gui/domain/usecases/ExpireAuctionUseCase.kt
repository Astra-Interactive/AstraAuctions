package com.astrainteractive.astramarket.gui.domain.usecases

import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.plugin.PluginPermission
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.util.displayNameOrMaterialName
import com.astrainteractive.astramarket.util.itemStack
import com.astrainteractive.astramarket.util.owner
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
class ExpireAuctionUseCase(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val serializer: Serializer
) : UseCase.Parametrized<ExpireAuctionUseCase.Params, Boolean> {
    class Params(
        val auction: AuctionDTO,
        val player: Player? = null
    )

    override suspend operator fun invoke(input: Params): Boolean {
        val player = input.player
        val receivedAuction = input.auction
        if (player != null && !PluginPermission.Expire.hasPermission(player)) {
            player.sendMessage(translation.noPermissions)
            return false
        }
        val auction = dataSource.fetchAuction(receivedAuction.id) ?: return false
        auction.owner?.player?.sendMessage(
            translation.notifyAuctionExpired
                .replace("%item%", auction.itemStack(serializer).displayNameOrMaterialName())
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
