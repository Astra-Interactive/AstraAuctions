package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.DataSourceModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.displayNameOrMaterialName
import com.astrainteractive.astratemplate.utils.itemStack
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astralibs.utils.economy.VaultEconomyProvider
import java.util.*

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
class AuctionBuyUseCase : IUseCase<Boolean, AuctionBuyUseCase.Params> {
    private val dataSource by DataSourceModule
    private val translation by TranslationModule
    private val config by ConfigModule
    class Params(
        val auction: AuctionDTO,
        val player: Player
    )
    override suspend fun run(params: Params): Boolean {
        val _auction = params.auction
        val player = params.player
        val auction = dataSource.fetchAuction(_auction.id) ?: return false
        if (auction.minecraftUuid == player.uniqueId.toString()) {
            player.sendMessage(translation.ownerCantBeBuyer)
            return false
        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack


        if (player.inventory.firstEmpty() == -1) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.inventoryFull)
            return false
        }
        var vaultResponse = VaultEconomyProvider.takeMoney(player, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.notEnoughMoney)
            return false
        }
        vaultResponse = VaultEconomyProvider.addMoney(owner, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.failedToPay)
            VaultEconomyProvider.addMoney(player, auction.price.toDouble())
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
            VaultEconomyProvider.addMoney(player, auction.price.toDouble())
            VaultEconomyProvider.takeMoney(owner, auction.price.toDouble())
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.dbError)
        }
        return result != null
    }
}