package ru.astrainteractive.astramarket.gui.api.market.usecase

import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.gui.api.market.api.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

class RemoveAuctionUseCase(
    private val auctionsBridge: AuctionsBridge,
    private val marketRepository: MarketRepository,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
    configKrate: CachedKrate<PluginConfig>,
) {
    private val translation by pluginTranslationKrate
    private val config by configKrate

    suspend operator fun invoke(auction: MarketSlot, playerUUID: UUID): Boolean {
        val storedAuction = marketRepository.findSlot(auction.id).getOrNull() ?: return false
        if (!storedAuction.isOwnedBy(playerUUID)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.remove.notOwner }
            return false
        }
        if (auctionsBridge.isInventoryFull(playerUUID)) {
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.error.inventoryFull }
            return false
        }
        if (marketRepository.deleteSlot(storedAuction).isFailure) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.error.unexpected }
            return false
        }
        playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.remove.removed }
        auctionsBridge.addItemToInventory(storedAuction, playerUUID)
        return true
    }
}
