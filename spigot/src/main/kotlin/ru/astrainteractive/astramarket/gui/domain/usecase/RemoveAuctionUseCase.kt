package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.Bukkit
import ru.astrainteractive.astramarket.gui.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.gui.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface RemoveAuctionUseCase : UseCase.Parametrized<RemoveAuctionUseCase.Params, Boolean> {
    data class Params(
        val auction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO,
        val playerUUID: UUID
    )
}

internal class RemoveAuctionUseCaseImpl(
    private val auctionsRepository: AuctionsRepository,
    private val playerInteraction: PlayerInteraction,
    private val translation: Translation,
    private val config: AuctionConfig,
) : RemoveAuctionUseCase {

    override suspend operator fun invoke(input: RemoveAuctionUseCase.Params): Boolean {
        val receivedAuction = input.auction
        val playerUUID = input.playerUUID
        val auction = auctionsRepository.getAuctionOrNull(receivedAuction.id) ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != playerUUID) {
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.notAuctionOwner
            }
            return false
        }

        if (auctionsRepository.isInventoryFull(playerUUID)) {
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.inventoryFull }
            return false
        }

        val result = auctionsRepository.deleteAuction(auction)
        return if (result != null) {
            playerInteraction.sendTranslationMessage(playerUUID, translation.auction.auctionDeleted)
            auctionsRepository.addItemToInventory(auction, playerUUID)
            true
        } else {
            playerInteraction.sendTranslationMessage(playerUUID, translation.general.unexpectedError)
            false
        }
    }
}
