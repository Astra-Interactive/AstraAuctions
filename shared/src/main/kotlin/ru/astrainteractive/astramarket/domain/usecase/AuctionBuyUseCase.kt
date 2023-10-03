package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.data.AuctionsRepository
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
interface AuctionBuyUseCase : UseCase.Parametrized<AuctionBuyUseCase.Params, Boolean> {
    class Params(
        val auction: AuctionDTO,
        val playerUUID: UUID
    )
}

internal class AuctionBuyUseCaseImpl(
    private val auctionsRepository: AuctionsRepository,
    private val playerInteraction: PlayerInteraction,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val economyProvider: EconomyProvider,
) : AuctionBuyUseCase {

    @Suppress("LongMethod")
    override suspend operator fun invoke(input: AuctionBuyUseCase.Params): Boolean {
        val receivedAuction = input.auction
        val playerUUID = input.playerUUID
        val playerName = auctionsRepository.playerName(playerUUID)
        val ownerUUID = receivedAuction.minecraftUuid.let(UUID::fromString)
        val ownerName = auctionsRepository.playerName(ownerUUID)
        val auction = auctionsRepository.getAuctionOrNull(receivedAuction.id) ?: return false
        if (auction.minecraftUuid == playerUUID.toString()) {
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.ownerCantBeBuyer
            }
            return false
        }
        if (auctionsRepository.isInventoryFull(playerUUID)) {
            playerInteraction.playSound(playerUUID) {
                config.sounds.fail
            }
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.inventoryFull
            }
            return false
        }
        var vaultResponse = economyProvider.takeMoney(playerUUID, auction.price.toDouble())
        if (!vaultResponse) {
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.notEnoughMoney
            }
            return false
        }
        vaultResponse = economyProvider.addMoney(ownerUUID, auction.price.toDouble())
        if (!vaultResponse) {
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            playerInteraction.sendTranslationMessage(playerUUID) { translation.auction.failedToPay }
            economyProvider.addMoney(playerUUID, auction.price.toDouble())
            return false
        }

        val result = auctionsRepository.deleteAuction(auction)
        if (result != null) {
            auctionsRepository.addItemToInventory(auction, playerUUID)
            playerInteraction.playSound(playerUUID) { config.sounds.sold }
            val itemName = auctionsRepository.itemDesc(auction)
            playerInteraction.sendTranslationMessage(playerUUID) {
                translation.auction.notifyUserBuy
                    .replace(
                        "%player_owner%",
                        ownerName ?: "-"
                    )
                    .replace("%price%", auction.price.toString())
                    .replace("%item%", itemName)
            }
            playerInteraction.sendTranslationMessage(ownerUUID) {
                translation.auction.notifyOwnerUserBuy
                    .replace("%player%", playerName ?: "-")
                    .replace("%item%", itemName)
                    .replace("%price%", auction.price.toString())
            }
        } else {
            economyProvider.addMoney(playerUUID, auction.price.toDouble())
            economyProvider.takeMoney(ownerUUID, auction.price.toDouble())
            playerInteraction.playSound(playerUUID) { config.sounds.fail }
            playerInteraction.sendTranslationMessage(playerUUID) { translation.general.dbError }
        }
        return result != null
    }
}
