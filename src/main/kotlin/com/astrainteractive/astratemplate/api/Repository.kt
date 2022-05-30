package com.astrainteractive.astratemplate.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.sqldatabase.Database
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object Repository {
    final const val TAG = "api"
    val api: AuctionAPI
        get() = AuctionAPI

    /**
     * @param uuid uuid of user, which auctions to load or null for every auction
     * @return current auction list
     */
    suspend fun loadAuctions(uuid: String? = null): List<Auction> {
        return api.getAuctions(uuid) ?: listOf()
    }

    var job: Timer? = null

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        Logger.log("Expired auction checker job has started", TAG, consolePrint = false)
        job = kotlin.concurrent.timer("auction_checker", daemon = true, 0L, 60000L) {
            if (!Database.isInitialized)
                return@timer
            if (!Database.isUpdated)
                return@timer
            AsyncHelper.launch {
                val auctions = api.getAuctions()
                auctions?.forEach {
                    if (System.currentTimeMillis() - it.time < AstraMarket.pluginConfig.auction.maxTime * 1000)
                        return@forEach
                    val res = forceExpireAuction(null, it)
                    Logger.log("Found expired auction ${it}. Expiring result: $res", TAG, consolePrint = false)
                }
            }
        }

    }

    fun stopAuctionChecker() {
        Logger.log("Expired auction checker job has stopped", TAG, consolePrint = false)
        job?.cancel()
    }

    /**
     * @param player admin or moderator
     * @param _auction auction to expire
     * @return boolean - true if success false if not
     */
    suspend fun forceExpireAuction(player: Player?, _auction: Auction): Boolean {
        if (player != null && !player.hasPermission(Permissions.expire)) {
            player.sendMessage(Translation.noPermissions)
            return false
        }
        Logger.log("Player ${player?.name} forced auction to expire ${_auction}", TAG, consolePrint = false)
        val auction = api.getAuction(_auction.id)?.firstOrNull() ?: return false
        auction.owner?.player?.sendMessage(
            Translation.notifyAuctionExpired
                .replace("%item%", auction.itemStack.displayNameOrMaterialName())
                .replace("%price%", auction.price.toString())
        )
        val result = api.expireAuction(auction)
        if (result == null) {
            player?.sendMessage(Translation.unexpectedError)
        } else
            player?.sendMessage(Translation.auctionHasBeenExpired)
        return (result != null)
    }

    /**
     * @param sortType type of sort [SortType]
     * @return result - list of sorted auctions. If [result] is null, [_currentAuctions] will be taken
     */
    fun sortBy(sortType: SortType, list: List<Auction>): List<Auction> {
        return when (sortType) {
            SortType.MATERIAL_DESC -> list.sortedByDescending { it.itemStack.type }
            SortType.MATERIAL_ASC -> list.sortedBy { it.itemStack.type }

            SortType.DATE_ASC -> list.sortedBy { it.time }
            SortType.DATE_DESC -> list.sortedByDescending { it.time }

            SortType.NAME_ASC -> list.sortedBy { it.itemStack.itemMeta?.displayName }
            SortType.NAME_DESC -> list.sortedByDescending { it.itemStack.itemMeta?.displayName }

            SortType.PRICE_ASC -> list.sortedBy { it.price }
            SortType.PRICE_DESC -> list.sortedByDescending { it.price }


            SortType.PLAYER_ASC -> list.sortedBy {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            SortType.PLAYER_DESC -> list.sortedByDescending {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            else -> list
        }
    }


    /**
     * @param _auction auction to remove
     * @param player owner of auction
     * @return boolean - true if succesfully removed
     */
    suspend fun removeAuction(_auction: Auction, player: Player): Boolean {
        val auction = api.getAuction(_auction.id)?.firstOrNull() ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            player.sendMessage(Translation.notAuctionOwner)
            return false
        }
        Logger.log("Player ${player.name} removed his auction ${auction}", TAG, consolePrint = false)
        val item = auction.itemStack
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(AstraMarket.pluginConfig.sounds.fail)
            player.sendMessage(Translation.inventoryFull)
            return false
        }
        val result = api.removeAuction(auction.id)
        return if (result != null) {
            player.sendMessage(Translation.auctionDeleted)
            player.inventory.addItem(item)
            true
        } else {
            player.sendMessage(Translation.unexpectedError)
            false
        }
    }


    /**
     * @param _auction auction to buy
     * @param player the player which will buy auction
     * @return boolean, which is true if succesfully bought
     */
    suspend fun buyAuction(_auction: Auction, player: Player): Boolean {
        val auction = api.getAuction(_auction.id)?.firstOrNull() ?: return false
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

        val result = api.removeAuction(auction.id)
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