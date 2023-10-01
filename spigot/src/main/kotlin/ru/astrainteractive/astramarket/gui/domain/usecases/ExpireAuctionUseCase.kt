package ru.astrainteractive.astramarket.gui.domain.usecases

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.displayNameOrMaterialName
import ru.astrainteractive.astramarket.util.itemStack
import ru.astrainteractive.astramarket.util.owner
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
interface ExpireAuctionUseCase : UseCase.Parametrized<ExpireAuctionUseCase.Params, Boolean> {
    class Params(
        val auction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO,
        val player: Player? = null
    )
}

internal class ExpireAuctionUseCaseImpl(
    private val dataSource: ru.astrainteractive.astramarket.api.market.AuctionsAPI,
    private val translation: Translation,
    private val serializer: Serializer
) : ExpireAuctionUseCase {

    override suspend operator fun invoke(input: ExpireAuctionUseCase.Params): Boolean {
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
