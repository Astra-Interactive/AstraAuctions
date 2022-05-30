package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class CreateAuctionUseCase : UseCase<Boolean, CreateAuctionUseCase.Params>() {
    class Params(
        val maxAuctionsAllowed: Int,
        val player: Player,
        val price: Float,
        val amount: Int,
        val item: ItemStack?
    )

    override suspend fun run(params: CreateAuctionUseCase.Params): Boolean {
        val player = params.player
        val maxAuctionsAllowed = params.maxAuctionsAllowed
        val price = params.price
        val item = params.item
        var amount = params.amount

        if (item == null || item.type == Material.AIR) {
            player.sendMessage(Translation.wrongItemInHand)
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            return false
        }

        amount = min(item.amount, amount)
        amount = max(amount, 1)

        val auctionsAmount = Repository.countPlayerAuctions(player) ?: 0
        if (auctionsAmount > maxAuctionsAllowed) {
            player.sendMessage(Translation.maxAuctions)
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            return false
        }
        if (price > AstraMarket.pluginConfig.auction.maxPrice || price < AstraMarket.pluginConfig.auction.minPrice) {
            player.sendMessage(Translation.wrongPrice)
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            return false
        }

        val itemClone = item.clone().apply { this.amount = amount }
        val auction = Auction(player, itemClone, price)

        val result = Repository.insertAuction(auction)
        if (result != null) {
            item.amount -= amount
            player.sendMessage(Translation.auctionAdded)
            player.playSound(AstraMarket.pluginConfig.sounds.success)
            Repository.fetchAuctions()
            if (AstraMarket.pluginConfig.auction.announce)
                Bukkit.broadcastMessage(Translation.broadcast.replace("%player%", player.name))
            return true
        } else {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.dbError)
            return false
        }


    }
}