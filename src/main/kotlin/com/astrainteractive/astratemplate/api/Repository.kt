package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.api.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object Repository {
    final const val TAG = "api"
    private val api: AuctionAPI
        get() = AuctionAPI

    /**
     * @param uuid uuid of user, which auctions to load or null for every auction
     * @return current auction list
     */
    suspend fun fetchAuctions(uuid: String? = null,expired:Boolean = false) = api.fetchAuctions(uuid,expired) ?: listOf()

    suspend fun fetchAuction(id: Long): List<Auction>? = api.fetchAuction(id)

    suspend fun deleteAuction(auction: Auction): Boolean? = api.deleteAuction(auction)

    suspend fun expireAuction(auction: Auction) = api.expireAuction(auction)

    suspend fun countPlayerAuctions(player: Player) = api.countPlayerAuctions(player)

    suspend fun insertAuction(auction: Auction) = api.insertAuction(auction)

    suspend fun fetchOldAuctions(millis:Long) = api.fetchOldAuctions(millis)


    /**
     * @param sortType type of sort [AuctionSort]
     * @return result - list of sorted auctions. If [result] is null, [_currentAuctions] will be taken
     */
    fun sortBy(sortType: AuctionSort, list: List<Auction>): List<Auction> {
        return when (sortType) {
            AuctionSort.MATERIAL_DESC -> list.sortedByDescending { it.itemStack.type }
            AuctionSort.MATERIAL_ASC -> list.sortedBy { it.itemStack.type }

            AuctionSort.DATE_ASC -> list.sortedBy { it.time }
            AuctionSort.DATE_DESC -> list.sortedByDescending { it.time }

            AuctionSort.NAME_ASC -> list.sortedBy { it.itemStack.itemMeta?.displayName }
            AuctionSort.NAME_DESC -> list.sortedByDescending { it.itemStack.itemMeta?.displayName }

            AuctionSort.PRICE_ASC -> list.sortedBy { it.price }
            AuctionSort.PRICE_DESC -> list.sortedByDescending { it.price }


            AuctionSort.PLAYER_ASC -> list.sortedBy {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            AuctionSort.PLAYER_DESC -> list.sortedByDescending {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }
            else -> list
        }
    }
}