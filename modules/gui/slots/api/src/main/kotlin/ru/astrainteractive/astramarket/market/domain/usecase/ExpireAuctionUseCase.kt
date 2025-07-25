package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import java.util.UUID

/**
 * @param player admin or moderator
 * @param _auction auction to expire
 * @return boolean - true if success false if not
 */
interface ExpireAuctionUseCase {
    class Params(
        val auction: MarketSlot,
        val playerUUID: UUID
    )
    suspend operator fun invoke(input: ExpireAuctionUseCase.Params): Boolean
}

internal class ExpireAuctionUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val marketApi: MarketApi,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
) : ExpireAuctionUseCase {
    private val translation by pluginTranslationKrate

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
