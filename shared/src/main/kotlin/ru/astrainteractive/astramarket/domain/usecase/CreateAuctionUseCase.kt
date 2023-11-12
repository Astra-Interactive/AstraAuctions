package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.data.AuctionsBridge
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

interface CreateAuctionUseCase : UseCase.Suspended<CreateAuctionUseCase.Params, Boolean> {
    class Params(
        val playerUUID: UUID,
        val auctionDTO: AuctionDTO
    )
}

internal class CreateAuctionUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val playerInteractionBridge: PlayerInteractionBridge,
    private val translation: Translation,
    private val config: AuctionConfig,
) : CreateAuctionUseCase {

    override suspend operator fun invoke(input: CreateAuctionUseCase.Params): Boolean {
        val playerUUID = input.playerUUID
        val auction = input.auctionDTO

        if (!auctionsBridge.isItemValid(input.auctionDTO)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.wrongItemInHand }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            return false
        }

        val maxAuctionsAllowed = auctionsBridge.maxAllowedAuctionsForPlayer(playerUUID)
            ?: config.auction.maxAuctionPerPlayer
        val auctionsAmount = auctionsBridge.countPlayerAuctions(playerUUID)
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

        val result = auctionsBridge.insertAuction(auction)
        return if (result != null) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.auctionAdded }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.success }
            if (config.auction.announce) {
                val playerName = auctionsBridge.playerName(playerUUID) ?: "-"
                playerInteractionBridge.broadcast(translation.auction.broadcast.replace("%player%", playerName))
            }
            true
        } else {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.general.dbError }
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            false
        }
    }
}
