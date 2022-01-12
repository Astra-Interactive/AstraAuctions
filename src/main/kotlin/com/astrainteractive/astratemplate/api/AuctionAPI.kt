package com.astrainteractive.astratemplate.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.sqldatabase.Database
import com.astrainteractive.astratemplate.sqldatabase.Repository
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object AuctionAPI {
    final const val TAG = "AuctionAPI"
    /**
     * Current auction list
     */
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
     * @return list of player's expired auctions
     */
    suspend fun getExpiredAuctions(uuid: String) = Repository.getAuctions(uuid, expired=true)

    /**
     * @param uuid uuid of user, which auctions to load or null for every auction
     * @return current auction list
     */
    suspend fun loadAuctions(uuid: String? = null): List<Auction> {
        val auctions = Repository.getAuctions(uuid) ?: listOf()
        setAuctions(auctions)
        return auctions
    }

    var job:Timer? = null

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        Logger.log("Expired auction checker job has started", TAG,consolePrint = false)
        job = kotlin.concurrent.timer("auction_checker",daemon = true,0L,AstraMarket.pluginConfig.auction.maxTime*20) {
            if (!Database.isInitialized)
                return@timer
            AsyncHelper.launch {
                val auctions = Repository.getAuctions()
                auctions?.forEach {
                    if (System.currentTimeMillis() - it.time < AstraMarket.pluginConfig.auction.maxTime * 1000)
                        return@forEach
                    val res = forceExpireAuction(null, it)
                    Logger.log("Found expired auction ${it}. Expiring result: $res", TAG,consolePrint = false)
                }
            }
        }

    }
    fun stopAuctionChecker(){
        Logger.log("Expired auction checker job has stopped", TAG,consolePrint = false)
        job?.cancel()
    }
    /**
     * @param player admin or moderator
     * @param _auction auction to expire
     * @return boolean - true if success false if not
     */
    suspend fun forceExpireAuction(player: Player?, _auction: Auction): Boolean {
        if (player!=null && !player.hasPermission(Permissions.expire)) {
            player.sendMessage(Translation.instance.noPermissions)
            return false
        }
        Logger.log("Player ${player?.name} forced auction to expire ${_auction}", TAG,consolePrint = false)
        val auction= Repository.getAuction(_auction.id)?.firstOrNull()?:return false
        auction.owner?.player?.sendMessage(Translation.instance.notifyAuctionExpired
            .replace("%item%",auction.itemStack.displayNameOrMaterialName())
            .replace("%price%",auction.price.toString()))
        val result = Repository.expireAuction(auction)
        if (result == null) {
            player?.sendMessage(Translation.instance.unexpectedError)
        } else
            player?.sendMessage(Translation.instance.auctionHasBeenExpired)
        return (result != null)
    }

    /**
     * @param sortType type of sort [SortType]
     * @return result - list of sorted auctions. If [result] is null, [currentAuctions] will be taken
     */
    fun sortBy(sortType: SortType, list: List<Auction>? = null): List<Auction> {
        val itemsInGui = list ?: getAuctions()
        return when (sortType) {
            SortType.MATERIAL_DESC -> itemsInGui.sortedByDescending { it.itemStack.type }
            SortType.MATERIAL_ASC -> itemsInGui.sortedBy { it.itemStack.type }

            SortType.DATE_ASC -> itemsInGui.sortedBy { it.time }
            SortType.DATE_DESC -> itemsInGui.sortedByDescending { it.time }

            SortType.NAME_ASC -> itemsInGui.sortedBy { it.itemStack.itemMeta?.displayName }
            SortType.NAME_DESC -> itemsInGui.sortedByDescending { it.itemStack.itemMeta?.displayName }

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



    /**
     * @param _auction auction to remove
     * @param player owner of auction
     * @return boolean - true if succesfully removed
     */
    suspend fun removeAuction(_auction: Auction, player: Player): Boolean {
        val auction = Repository.getAuction(_auction.id)?.firstOrNull() ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            player.sendMessage(Translation.instance.notAuctionOwner)
            return false
        }
        Logger.log("Player ${player.name} removed his auction ${auction}", TAG,consolePrint = false)
        val item = auction.itemStack
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
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
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.inventoryFull)
            return false
        }
        var vaultResponse = VaultHook.takeMoney(player, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.notEnoughMoney)
            return false
        }
        vaultResponse = VaultHook.addMoney(owner, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.failedToPay)
            VaultHook.addMoney(player, auction.price.toDouble())
            return false
        }

        val result = Repository.removeAuction(auction.id)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(AstraMarket.pluginConfig.sounds.sold)
            player.sendMessage(
                Translation.instance.notifyUserBuy.replace(
                    "%player_owner%",
                    owner.name ?: "${ChatColor.MAGIC}NULL"
                ).replace("%price%", auction.price.toString()).replace("%item%", item.displayNameOrMaterialName())
            )
            owner.player?.sendMessage(
                Translation.instance.notifyOwnerUserBuy.replace("%player%", player.name)
                    .replace("%item%", item.displayNameOrMaterialName()).replace("%price%", auction.price.toString())
            )
        } else {
            VaultHook.addMoney(player, auction.price.toDouble())
            VaultHook.takeMoney(owner, auction.price.toDouble())
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.instance.dbError)
        }
        return result != null
    }


}