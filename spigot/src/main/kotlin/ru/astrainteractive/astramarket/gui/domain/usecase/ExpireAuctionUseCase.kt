package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.itemStack
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.owner
import ru.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.util.displayNameOrMaterialName
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
interface ExpireAuctionUseCase : UseCase.Parametrized<ExpireAuctionUseCase.Params, Boolean> {
    class Params(
        val auction: AuctionDTO,
        val player: Player? = null
    )
}

internal class ExpireAuctionUseCaseImpl(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val serializer: Encoder,
    private val permissionManager: PermissionManager,
    private val stringSerializer: KyoriComponentSerializer
) : ExpireAuctionUseCase {

    override suspend operator fun invoke(input: ExpireAuctionUseCase.Params): Boolean {
        val player = input.player
        val receivedAuction = input.auction

        if (player != null && !permissionManager.hasPermission(player.uniqueId, PluginPermission.Expire)) {
            stringSerializer.sendMessage(translation.general.noPermissions, player)
            return false
        }
        val auction = dataSource.fetchAuction(receivedAuction.id) ?: return false
        stringSerializer.sendMessage(
            translation.auction.notifyAuctionExpired
                .replace("%item%", auction.itemStack(serializer).displayNameOrMaterialName())
                .replace("%price%", auction.price.toString()),
            auction.owner?.player
        )
        val result = dataSource.expireAuction(auction)
        if (result == null) {
            stringSerializer.sendMessage(translation.general.unexpectedError, player)
        } else {
            stringSerializer.sendMessage(translation.auction.auctionHasBeenExpired, player)
        }
        return (result != null)
    }
}
