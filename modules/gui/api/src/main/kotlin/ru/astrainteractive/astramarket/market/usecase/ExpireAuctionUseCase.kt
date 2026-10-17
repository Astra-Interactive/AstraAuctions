package ru.astrainteractive.astramarket.market.usecase

import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

class ExpireAuctionUseCase(
    private val auctionsBridge: AuctionsBridge,
    private val marketRepository: MarketRepository,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
) {
    private val translation by pluginTranslationKrate

    suspend operator fun invoke(auction: MarketSlot, playerUUID: UUID): Boolean {
        if (!auctionsBridge.hasExpirePermission(playerUUID)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.error.noPermission }
            return false
        }
        val storedAuction = marketRepository.getSlot(auction.id).getOrNull() ?: return false
        val itemName = auctionsBridge.itemDesc(storedAuction)
        playerInteractionBridge.sendTranslationMessage(UUID.fromString(auction.minecraftUuid)) {
            translation.expire.ownerNotice(itemName = itemName, price = storedAuction.price)
        }
        val isExpired = marketRepository.expireSlot(storedAuction).isSuccess
        playerInteractionBridge.sendTranslationMessage(playerUUID) {
            if (isExpired) translation.expire.success else translation.error.unexpected
        }
        return isExpired
    }
}
