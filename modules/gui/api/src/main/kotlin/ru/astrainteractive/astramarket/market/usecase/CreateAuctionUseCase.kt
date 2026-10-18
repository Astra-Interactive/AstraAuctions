package ru.astrainteractive.astramarket.market.usecase

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge
import ru.astrainteractive.astramarket.market.util.canListMore
import ru.astrainteractive.astramarket.market.util.isPriceAllowed
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

class CreateAuctionUseCase(
    private val auctionsBridge: AuctionsBridge,
    private val marketRepository: MarketRepository,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
    configKrate: CachedKrate<PluginConfig>,
) {
    private val translation by pluginTranslationKrate
    private val config by configKrate

    private fun fail(playerUUID: UUID, message: () -> LocalizableComponent): Boolean {
        playerInteractionBridge.sendTranslationMessage(playerUUID, message)
        playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
        return false
    }

    suspend operator fun invoke(marketSlot: MarketSlot, playerUUID: UUID): Boolean {
        if (!auctionsBridge.isItemValid(marketSlot)) {
            return fail(playerUUID) { translation.sell.wrongItem }
        }
        val listedAmount = marketRepository.countPlayerSlots(playerUUID.toString()).getOrDefault(0)
        val permissionLimit = auctionsBridge.maxAllowedAuctionsForPlayer(playerUUID)
        if (!config.auction.canListMore(listedAmount, permissionLimit)) {
            return fail(playerUUID) { translation.sell.tooManyLots }
        }
        if (!config.auction.isPriceAllowed(marketSlot.price)) {
            return fail(playerUUID) { translation.sell.wrongPrice }
        }
        if (marketRepository.insertSlot(marketSlot).isFailure) {
            return fail(playerUUID) { translation.error.database }
        }
        playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.sell.listed }
        playerInteractionBridge.playSound(playerUUID) { config.sounds.success }
        if (config.auction.announce) {
            val playerName = auctionsBridge.playerName(playerUUID) ?: "-"
            playerInteractionBridge.broadcast(translation.sell.announcement(playerName))
        }
        return true
    }
}
