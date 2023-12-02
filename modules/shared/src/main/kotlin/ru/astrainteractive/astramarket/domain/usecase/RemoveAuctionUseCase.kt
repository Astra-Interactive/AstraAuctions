package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astramarket.data.AuctionsBridge
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface RemoveAuctionUseCase : UseCase.Suspended<RemoveAuctionUseCase.Params, Boolean> {
    data class Params(
        val auction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO,
        val playerUUID: UUID
    )
}

internal class RemoveAuctionUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val playerInteractionBridge: PlayerInteractionBridge,
    private val translation: Translation,
    private val config: AuctionConfig,
) : RemoveAuctionUseCase {

    override suspend operator fun invoke(input: RemoveAuctionUseCase.Params): Boolean {
        val receivedAuction = input.auction
        val playerUUID = input.playerUUID
        val auction = auctionsBridge.getAuctionOrNull(receivedAuction.id) ?: return false
        val ownerUUID = auction.minecraftUuid.let(UUID::fromString)
        if (ownerUUID != playerUUID) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.notAuctionOwner
            }
            return false
        }

        if (auctionsBridge.isInventoryFull(playerUUID)) {
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.inventoryFull }
            return false
        }

        val result = auctionsBridge.deleteAuction(auction)
        return if (result != null) {
            playerInteractionBridge.sendTranslationMessage(playerUUID, translation.auction.auctionDeleted)
            auctionsBridge.addItemToInventory(auction, playerUUID)
            true
        } else {
            playerInteractionBridge.sendTranslationMessage(playerUUID, translation.general.unexpectedError)
            false
        }
    }
}
