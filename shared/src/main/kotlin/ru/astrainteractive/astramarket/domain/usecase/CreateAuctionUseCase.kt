package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

interface CreateAuctionUseCase : UseCase.Parametrized<CreateAuctionUseCase.Params, Boolean> {
    class Params(
        val playerUUID: UUID,
        val auctionDTO: AuctionDTO
    )
}

internal class CreateAuctionUseCaseImpl(
    private val auctionsRepository: AuctionsRepository,
    private val playerInteraction: PlayerInteraction,
    private val translation: Translation,
    private val config: AuctionConfig,
) : CreateAuctionUseCase {

    override suspend operator fun invoke(input: CreateAuctionUseCase.Params): Boolean {
        val playerUUID = input.playerUUID
        val auction = input.auctionDTO

        if (!auctionsRepository.isItemValid(input.auctionDTO)) {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.wrongItemInHand }
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            return false
        }

        val maxAuctionsAllowed = auctionsRepository.maxAllowedAuctionsForPlayer(playerUUID)
            ?: config.auction.maxAuctionPerPlayer
        val auctionsAmount = auctionsRepository.countPlayerAuctions(playerUUID)
        if (auctionsAmount >= maxAuctionsAllowed) {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.maxAuctions }
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            return false
        }
        if (auction.price > config.auction.maxPrice || auction.price < config.auction.minPrice) {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.wrongPrice }
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            return false
        }

        val result = auctionsRepository.insertAuction(auction)
        return if (result != null) {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.auctionAdded }
            playerInteraction.playSound(playerUUID) { config.sounds.success }
            if (config.auction.announce) {
                val playerName = auctionsRepository.playerName(playerUUID) ?: "-"
                playerInteraction.broadcast(translation.auction.broadcast.replace("%player%", playerName))
            }
            true
        } else {
            playerInteraction.sendTranslationMessage(playerUUID) { translation.general.dbError }
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            false
        }
    }
}
