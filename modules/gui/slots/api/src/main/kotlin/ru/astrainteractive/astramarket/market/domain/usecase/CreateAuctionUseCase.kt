package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import java.util.UUID

interface CreateAuctionUseCase {
    class Params(
        val playerUUID: UUID,
        val marketSlot: MarketSlot
    )
    suspend operator fun invoke(input: CreateAuctionUseCase.Params): Boolean
}

internal class CreateAuctionUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val marketApi: MarketApi,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
    configKrate: CachedKrate<PluginConfig>,
) : CreateAuctionUseCase {
    private val translation by pluginTranslationKrate
    private val config by configKrate

    override suspend operator fun invoke(input: CreateAuctionUseCase.Params): Boolean {
        val playerUUID = input.playerUUID
        val auction = input.marketSlot

        if (!auctionsBridge.isItemValid(input.marketSlot)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.wrongItemInHand }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            return false
        }

        val maxAuctionsAllowed = auctionsBridge.maxAllowedAuctionsForPlayer(playerUUID)
            ?: config.auction.maxAuctionPerPlayer
        val auctionsAmount = marketApi.countPlayerSlots(playerUUID.toString()) ?: 0
        if (auctionsAmount >= maxAuctionsAllowed) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.maxAuctions }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            return false
        }
        if (auction.price > config.auction.maxPrice || auction.price < config.auction.minPrice) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.wrongPrice }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            return false
        }

        val result = marketApi.insertSlot(auction)
        return if (result != null) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.auctionAdded }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.success }
            if (config.auction.announce) {
                val playerName = auctionsBridge.playerName(playerUUID) ?: "-"
                playerInteractionBridge.broadcast(translation.auction.broadcast(playerName))
            }
            true
        } else {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.general.dbError }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            false
        }
    }
}
