package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
interface ExpireAuctionUseCase : UseCase.Suspended<ExpireAuctionUseCase.Params, Boolean> {
    class Params(
        val auction: MarketSlot,
        val playerUUID: UUID
    )
}

internal class ExpireAuctionUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val marketApi: MarketApi,
    private val playerInteractionBridge: PlayerInteractionBridge,
    private val translation: Translation,
) : ExpireAuctionUseCase {

    override suspend operator fun invoke(input: ExpireAuctionUseCase.Params): Boolean {
        val playerUUID = input.playerUUID
        val receivedAuction = input.auction
        val ownerUUID = receivedAuction.minecraftUuid.let(UUID::fromString)

        if (!auctionsBridge.hasExpirePermission(playerUUID)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.general.noPermissions }
            return false
        }
        val auction = marketApi.getSlot(receivedAuction.id) ?: return false
        val itemName = auctionsBridge.itemDesc(auction)
        playerInteractionBridge.sendTranslationMessage(ownerUUID) {
            translation.auction.notifyAuctionExpired
                .replace("%item%", itemName)
                .replace("%price%", auction.price.toString())
        }

        val result = marketApi.expireSlot(auction)
        if (result == null) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.general.unexpectedError
            }
        } else {
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.auctionHasBeenExpired
            }
        }
        return (result != null)
    }
}
