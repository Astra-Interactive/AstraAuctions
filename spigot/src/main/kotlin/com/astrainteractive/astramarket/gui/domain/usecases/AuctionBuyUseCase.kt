package com.astrainteractive.astramarket.gui.domain.usecases

import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.util.displayNameOrMaterialName
import com.astrainteractive.astramarket.util.itemStack
import com.astrainteractive.astramarket.util.playSound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import java.util.UUID

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
class AuctionBuyUseCase(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val economyProvider: EconomyProvider,
    private val serializer: Serializer
) : UseCase<Boolean, AuctionBuyUseCase.Params> {

    class Params(
        val auction: AuctionDTO,
        val player: Player
    )

    override suspend fun run(params: Params): Boolean {
        val receivedAuction = params.auction
        val player = params.player
        val auction = dataSource.fetchAuction(receivedAuction.id) ?: return false
        if (auction.minecraftUuid == player.uniqueId.toString()) {
            player.sendMessage(translation.ownerCantBeBuyer)
            return false
        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack(serializer)

        if (player.inventory.firstEmpty() == -1) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.inventoryFull)
            return false
        }
        var vaultResponse = economyProvider.takeMoney(player.uniqueId, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.notEnoughMoney)
            return false
        }
        vaultResponse = economyProvider.addMoney(owner.uniqueId, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.failedToPay)
            economyProvider.addMoney(player.uniqueId, auction.price.toDouble())
            return false
        }

        val result = dataSource.deleteAuction(auction)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(config.sounds.sold)
            player.sendMessage(
                translation.notifyUserBuy.replace(
                    "%player_owner%",
                    owner.name ?: "${ChatColor.MAGIC}NULL"
                ).replace("%price%", auction.price.toString()).replace("%item%", item.displayNameOrMaterialName())
            )
            owner.player?.sendMessage(
                translation.notifyOwnerUserBuy.replace("%player%", player.name)
                    .replace("%item%", item.displayNameOrMaterialName()).replace("%price%", auction.price.toString())
            )
        } else {
            economyProvider.addMoney(player.uniqueId, auction.price.toDouble())
            economyProvider.takeMoney(owner.uniqueId, auction.price.toDouble())
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.dbError)
        }
        return result != null
    }
}
