package ru.astrainteractive.astramarket.gui.api.market.usecase

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astramarket.core.economy.api.CurrencyEconomyProviderResolver
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.gui.api.market.api.PlayerInteractionBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import java.util.UUID

class BuyAuctionUseCase(
    private val auctionsBridge: AuctionsBridge,
    private val marketRepository: MarketRepository,
    private val playerInteractionBridge: PlayerInteractionBridge,
    pluginTranslationKrate: CachedKrate<PluginTranslation>,
    configKrate: CachedKrate<PluginConfig>,
    private val economyProviderFactory: CurrencyEconomyProviderResolver,
) {
    private val config by configKrate
    private val translation by pluginTranslationKrate

    private fun fail(playerUUID: UUID, message: () -> LocalizableComponent) {
        playerInteractionBridge.playSound(playerUUID) { config.sounds.fail }
        playerInteractionBridge.sendTranslationMessage(playerUUID, message)
    }

    suspend operator fun invoke(auction: MarketSlot, playerUUID: UUID): Boolean {
        val economyProvider = economyProviderFactory.findDefault() ?: return false
        val ownerUUID = UUID.fromString(auction.minecraftUuid)
        val playerName = auctionsBridge.playerName(playerUUID)
        val ownerName = auctionsBridge.playerName(ownerUUID)
        val storedAuction = marketRepository.findSlot(auction.id).getOrNull() ?: return false
        if (storedAuction.isOwnedBy(playerUUID)) {
            playerInteractionBridge.sendTranslationMessage(playerUUID) { translation.buy.ownLot }
            return false
        }
        if (auctionsBridge.isInventoryFull(playerUUID)) {
            fail(playerUUID) { translation.error.inventoryFull }
            return false
        }
        val price = storedAuction.price.toDouble()
        if (!economyProvider.takeMoney(playerUUID, price)) {
            fail(playerUUID) { translation.buy.notEnoughMoney }
            return false
        }
        if (!economyProvider.addMoney(ownerUUID, price)) {
            fail(playerUUID) { translation.buy.paymentFailed }
            economyProvider.addMoney(playerUUID, price)
            return false
        }
        if (marketRepository.deleteSlot(storedAuction).isFailure) {
            economyProvider.addMoney(playerUUID, price)
            economyProvider.takeMoney(ownerUUID, price)
            fail(playerUUID) { translation.error.database }
            return false
        }
        auctionsBridge.addItemToInventory(storedAuction, playerUUID)
        playerInteractionBridge.playSound(playerUUID) { config.sounds.sold }
        val itemName = auctionsBridge.itemDesc(storedAuction)
        playerInteractionBridge.sendTranslationMessage(playerUUID) {
            translation.buy.bought(
                sellerName = ownerName ?: "-",
                itemName = itemName,
                price = storedAuction.price
            )
        }
        playerInteractionBridge.sendTranslationMessage(ownerUUID) {
            translation.buy.sold(
                buyerName = playerName ?: "-",
                itemName = itemName,
                price = storedAuction.price
            )
        }
        return true
    }
}
