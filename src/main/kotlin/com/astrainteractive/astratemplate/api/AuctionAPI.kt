package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.api.entities.Auction
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.database.columnName
import ru.astrainteractive.astralibs.utils.catching
import ru.astrainteractive.astralibs.utils.forEach
import ru.astrainteractive.astralibs.utils.uuid


/**
 * Repository with all SQL commands
 */
object AuctionAPI {

    val database: Database?
        get() = Database.instance


    suspend fun insertAuction(auction: Auction) = catching() {
        database?.insert(auction,)
    }

    suspend fun expireAuction(auction: Auction) = catching() {
        database?.update(auction.copy(expired = true),)
    }

    suspend fun fetchAuctions(uuid: String? = null, expired: Boolean? = false) = catching() {
        val where =
            uuid?.let { "WHERE ${Auction::minecraftUuid.columnName}=\'${it}\' AND ${Auction::expired.columnName} = ${expired}" }
                ?: "WHERE ${Auction::expired.columnName} = $expired"
        database?.select<Auction>(where,)
    }

    suspend fun fetchOldAuctions(millis: Long) = catching(true) {
        val currentTime = System.currentTimeMillis()
        val where = "WHERE ($currentTime - ${Auction::time.columnName}) > $millis AND ${Auction::expired.columnName} = 0"
        database?.select<Auction>(where)
    }

    suspend fun fetchAuction(id: Long) = catching() {
        database?.select<Auction>("WHERE ${Auction::id.columnName}=$id")
    }

    suspend fun deleteAuction(auction: Auction): Boolean? = catching() {
        database?.delete(auction)
    }

    suspend fun countPlayerAuctions(player: Player) = catching() {
        val query =
            "SELECT COUNT(*) FROM ${Auction.TABLE} WHERE ${Auction::minecraftUuid.columnName}=\'${player.uuid}\' AND ${Auction::expired.columnName}=0"
        val response = database?.connection?.createStatement()?.executeQuery(query)
        response?.forEach { return@catching it.getInt(1) + 1 }
        return@catching null
    }

}

