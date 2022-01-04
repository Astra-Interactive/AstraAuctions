package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.sqldatabase.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.Translation
import com.astrainteractive.astratemplate.utils.VaultHook
import com.astrainteractive.astratemplate.utils.displayNameOrMaterialName
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.Exception
import java.util.*

object AuctionAPI {

    private var currentAuctions: List<Auction> = listOf()

    private fun setAuctions(value: List<Auction>) = synchronized(this) {
        currentAuctions = value
    }

    private fun getAuctions() = synchronized(this) { currentAuctions }


    suspend fun loadAuctions(uuid: String? = null): List<Auction> {
        val auctions = Repository.getAuctions(uuid) ?: listOf()
        setAuctions(auctions)
        return currentAuctions
    }

    fun sortBy(sortType: SortType): List<Auction> {
        val itemsInGui = getAuctions()
        return when (sortType) {
            SortType.MATERIAL_DESC -> itemsInGui.sortedByDescending { it.itemStack.type }
            SortType.MATERIAL_ASC -> itemsInGui.sortedBy { it.itemStack.type }

            SortType.DATE_ASC -> itemsInGui.sortedBy { it.time }
            SortType.DATE_DESC -> itemsInGui.sortedByDescending { it.time }

            SortType.NAME_ASC -> itemsInGui.sortedBy { it.itemStack.itemMeta.displayName }
            SortType.NAME_DESC -> itemsInGui.sortedByDescending { it.itemStack.itemMeta.displayName }
            else -> itemsInGui
        }
    }

    suspend fun buyAuction(_auction: Auction, player: Player): Boolean {
        val auction = Repository.getAuction(_auction.id)?.firstOrNull() ?: return false
//        if (auction.minecraftUuid == player.uniqueId.toString()) {
//            player.sendMessage(Translation.instanse.ownerCantBeBuyer)
//            return false
//        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack


        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instanse.inventoryFull)
            return false
        }
        var vaultResponse = VaultHook.takeMoney(player, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instanse.notEnoughMoney)
            return false
        }
        vaultResponse = VaultHook.addMoney(owner, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instanse.failedToPay)
            VaultHook.addMoney(player, auction.price.toDouble())
            return false
        }

        val result = Repository.removeAuction(auction.id)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(AstraAuctions.pluginConfig.sounds.sold)
            player.sendMessage(Translation.instanse.itemBought + "-> ${owner.name} -> ${item.displayNameOrMaterialName()} -> ${auction.price}$")
        } else {
            VaultHook.addMoney(player, auction.price.toDouble())
            VaultHook.takeMoney(owner, auction.price.toDouble())
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instanse.dbError)
        }
        return result != null
    }


}