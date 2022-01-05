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
import java.util.*

object AuctionAPI {

    private var currentAuctions: List<Auction> = listOf()

    /**
     * Set current auction list
     */
    private fun setAuctions(value: List<Auction>) = synchronized(this) {
        currentAuctions = value
    }

    /**
     * @return current auctions list
     */
    private fun getAuctions() = synchronized(this) { currentAuctions }


    /**
     * @param uuid uuid of user, which auctions to load or null for every auction
     * @return list of auctions
     */
    suspend fun loadAuctions(uuid: String? = null): List<Auction> {
        val auctions = Repository.getAuctions(uuid) ?: listOf()
        setAuctions(auctions)
        return auctions
    }

    /**
     * @param sortType type of sort [SortType]
     * @return result - list of sorted auctions
     */
    fun sortBy(sortType: SortType): List<Auction> {
        val itemsInGui = getAuctions()
        return when (sortType) {
            SortType.MATERIAL_DESC -> itemsInGui.sortedByDescending { it.itemStack.type }
            SortType.MATERIAL_ASC -> itemsInGui.sortedBy { it.itemStack.type }

            SortType.DATE_ASC -> itemsInGui.sortedBy { it.time }
            SortType.DATE_DESC -> itemsInGui.sortedByDescending { it.time }

            SortType.NAME_ASC -> itemsInGui.sortedBy { it.itemStack.itemMeta.displayName }
            SortType.NAME_DESC -> itemsInGui.sortedByDescending { it.itemStack.itemMeta.displayName }

            SortType.PRICE_ASC -> itemsInGui.sortedBy { it.price }
            SortType.PRICE_DESC -> itemsInGui.sortedByDescending { it.price }


            SortType.PLAYER_ASC -> itemsInGui.sortedBy {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            SortType.PLAYER_DESC -> itemsInGui.sortedByDescending {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            else -> itemsInGui
        }
    }


    suspend fun removeAuction(_auction: Auction, player: Player): Boolean {
        val auction = Repository.getAuction(_auction.id)?.firstOrNull() ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            player.sendMessage(Translation.instance.notAuctionOwner)
            return false
        }
        val item = auction.itemStack
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.inventoryFull)
            return false
        }
        val result = Repository.removeAuction(auction.id)
        return if (result != null) {
            player.sendMessage(Translation.instance.auctionDeleted)
            player.inventory.addItem(item)
            true
        } else {
            player.sendMessage(Translation.instance.unexpectedError)
            false
        }

    }

    /**
     * @param _auction auction to buy
     * @param player the player which will buy auction
     * @return boolean, which is true if succesfully bought
     */
    suspend fun buyAuction(_auction: Auction, player: Player): Boolean {
        val auction = Repository.getAuction(_auction.id)?.firstOrNull() ?: return false
        if (auction.minecraftUuid == player.uniqueId.toString()) {
            player.sendMessage(Translation.instance.ownerCantBeBuyer)
            return false
        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack


        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.inventoryFull)
            return false
        }
        var vaultResponse = VaultHook.takeMoney(player, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.notEnoughMoney)
            return false
        }
        vaultResponse = VaultHook.addMoney(owner, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.failedToPay)
            VaultHook.addMoney(player, auction.price.toDouble())
            return false
        }

        val result = Repository.removeAuction(auction.id)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(AstraAuctions.pluginConfig.sounds.sold)
            player.sendMessage(
                Translation.instance.notifyUserBuy.replace(
                    "%player_owner%",
                    owner.name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                ).replace("%price%", auction.price.toString()).replace("%item%", item.displayNameOrMaterialName())
            )
            owner.player?.sendMessage(
                Translation.instance.notifyOwnerUserBuy.replace("%player%", player.name)
                    .replace("%item%", item.displayNameOrMaterialName()).replace("%price%", auction.price.toString())
            )
        } else {
            VaultHook.addMoney(player, auction.price.toDouble())
            VaultHook.takeMoney(owner, auction.price.toDouble())
            player.playSound(AstraAuctions.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.dbError)
        }
        return result != null
    }


}