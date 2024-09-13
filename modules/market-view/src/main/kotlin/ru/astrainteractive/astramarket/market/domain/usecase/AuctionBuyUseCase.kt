package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
interface AuctionBuyUseCase : UseCase.Suspended<AuctionBuyUseCase.Params, Boolean> {
    class Params(
        val auction: MarketSlot,
        val playerUUID: UUID
    )
}

internal class AuctionBuyUseCaseImpl(
    private val auctionsBridge: AuctionsBridge,
    private val marketApi: MarketApi,
    private val playerInteractionBridge: PlayerInteractionBridge,
    private val translation: Translation,
    private val config: PluginConfig,
    private val economyProvider: EconomyProvider,
) : AuctionBuyUseCase, Logger by JUtiltLogger("AuctionBuyUseCase") {

    @Suppress("LongMethod")
    override suspend operator fun invoke(input: AuctionBuyUseCase.Params): Boolean {
        val receivedAuction = input.auction
        val playerUUID = input.playerUUID
        val playerName = auctionsBridge.playerName(playerUUID)
        val ownerUUID = receivedAuction.minecraftUuid.let(UUID::fromString)
        val ownerName = auctionsBridge.playerName(ownerUUID)
        val auction = marketApi.getSlot(receivedAuction.id) ?: return false
        if (auction.minecraftUuid == playerUUID.toString()) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.ownerCantBeBuyer
            }
            return false
        }
        if (auctionsBridge.isInventoryFull(playerUUID)) {
            playerInteractionBridge.playSound(playerUUID) {
                config.sounds.fail
            }
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.inventoryFull
            }
            return false
        }
        var vaultResponse = economyProvider.takeMoney(playerUUID, auction.price.toDouble())
        if (!vaultResponse) {
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.notEnoughMoney
            }
            return false
        }
        vaultResponse = economyProvider.addMoney(ownerUUID, auction.price.toDouble())
        if (!vaultResponse) {
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.auction.failedToPay }
            economyProvider.addMoney(playerUUID, auction.price.toDouble())
            return false
        }

        val result = marketApi.deleteSlot(auction)
        if (result != null) {
            auctionsBridge.addItemToInventory(auction, playerUUID)
            playerInteractionBridge.playSound(playerUUID) { config.sounds.sold }
            val itemName = auctionsBridge.itemDesc(auction)
            playerInteractionBridge.sendTranslationMessage(playerUUID) {
                translation.auction.notifyUserBuy(
                    playerOwner = ownerName ?: "-",
                    itemName = itemName,
                    price = auction.price
                )
            }
            playerInteractionBridge.sendTranslationMessage(ownerUUID) {
                translation.auction.notifyOwnerUserBuy(
                    playerName = playerName ?: "-",
                    itemName = itemName,
                    price = auction.price
                )
            }
        } else {
            economyProvider.addMoney(playerUUID, auction.price.toDouble())
            economyProvider.takeMoney(ownerUUID, auction.price.toDouble())
            playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.general.dbError }
        }
        return result != null
    }
}
