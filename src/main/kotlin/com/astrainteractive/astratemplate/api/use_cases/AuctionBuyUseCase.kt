package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.VaultHook
import com.astrainteractive.astratemplate.utils.displayNameOrMaterialName
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
class AuctionBuyUseCase : UseCase<Boolean, AuctionBuyUseCase.Params>() {
    class Params(
        val auction: Auction,
        val player: Player
    )
    override suspend fun run(params: Params): Boolean {
        val _auction = params.auction
        val player = params.player
        val auction = Repository.fetchAuction(_auction.id)?.firstOrNull() ?: return false
        if (auction.minecraftUuid == player.uniqueId.toString()) {
            player.sendMessage(Translation.ownerCantBeBuyer)
            return false
        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack


        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.inventoryFull)
            return false
        }
        var vaultResponse = VaultHook.takeMoney(player, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.notEnoughMoney)
            return false
        }
        vaultResponse = VaultHook.addMoney(owner, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.failedToPay)
            VaultHook.addMoney(player, auction.price.toDouble())
            return false
        }

        val result = Repository.deleteAuction(auction.id)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(AstraMarket.pluginConfig.sounds.sold)
            player.sendMessage(
                Translation.notifyUserBuy.replace(
                    "%player_owner%",
                    owner.name ?: "${ChatColor.MAGIC}NULL"
                ).replace("%price%", auction.price.toString()).replace("%item%", item.displayNameOrMaterialName())
            )
            owner.player?.sendMessage(
                Translation.notifyOwnerUserBuy.replace("%player%", player.name)
                    .replace("%item%", item.displayNameOrMaterialName()).replace("%price%", auction.price.toString())
            )
        } else {
            VaultHook.addMoney(player, auction.price.toDouble())
            VaultHook.takeMoney(owner, auction.price.toDouble())
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.dbError)
        }
        return result != null
    }
}