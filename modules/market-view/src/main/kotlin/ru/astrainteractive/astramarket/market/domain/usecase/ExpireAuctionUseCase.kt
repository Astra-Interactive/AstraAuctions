package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.Krate
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
    translationKrate: Krate<Translation>,
) : ExpireAuctionUseCase {
    private val translation by translationKrate

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
            translation.auction.notifyAuctionExpired(
                item = itemName,
                price = auction.price
            )
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
