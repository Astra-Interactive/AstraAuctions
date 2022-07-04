package com.astrainteractive.astratemplate.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.sqldatabase.Database
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import org.bukkit.entity.Player



/**
 * Repository with all SQL commands
 */
object AuctionAPI {
    /**
     * Return boolean of null if exception happened
     */
    suspend fun createAuctionTable() =
        callbackCatching {
            return@callbackCatching Database.connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ${Auction.table} " +
                        "(" +
                        "${Auction.id.name} ${Auction.id.type} PRIMARY KEY AUTOINCREMENT, " +
                        "${Auction.discordId.name} ${Auction.discordId.type} NULL, " +
                        "${Auction.minecraftUuid.name} ${Auction.minecraftUuid.type} NOT NULL, " +
                        "${Auction.time.name} ${Auction.time.type} NOT NULL, " +
                        "${Auction.item.name} ${Auction.item.type} NOT NULL, " +
                        "${Auction.expired.name} ${Auction.expired.type} NOT NULL, " +
                        "${Auction.price.name} ${Auction.price.type} NOT NULL);"
            ).execute()
        }

    suspend fun insertAuction(auction: Auction) =
        callbackCatching {
            val query = "INSERT INTO ${Auction.table} " +
                    "(${Auction.discordId.name}, ${Auction.minecraftUuid.name}, ${Auction.time.name}, ${Auction.item.name}, ${Auction.price.name}, ${Auction.expired.name}) " +
                    "VALUES(NULL, \'${auction.minecraftUuid}\', ${auction.time},?, ${auction.price}, 0)"
            val statement = Database.connection.prepareStatement(query)
            statement.setBytes(1, auction.item)
            return@callbackCatching statement.executeUpdate()
        }

    suspend fun expireAuction(auction: Auction)= callbackCatching {
        val query = "UPDATE ${Auction.table} SET ${Auction.expired.name}=TRUE WHERE ${Auction.id.name}=${auction.id}"
        Database.connection.prepareStatement(query).execute()
    }

    suspend fun fetchAuctions(uuid: String? = null, expired:Boolean? = false) = callbackCatching {
        val where = uuid?.let { "WHERE ${Auction.minecraftUuid.name}=\'${it}\' AND ${Auction.expired.name} = ${expired}"  } ?: "WHERE ${Auction.expired.name} = ${expired}"
        val rs = Database.connection.createStatement().executeQuery("SELECT * FROM ${Auction.table} $where")
        return@callbackCatching rs.mapNotNull { Auction.fromResultSet(it) }
    }
    suspend fun fetchOldAuctions(millis:Long) = callbackCatching {
        val currentTime = System.currentTimeMillis()
        val where = "WHERE ($currentTime - ${Auction.time.name}) > $millis AND ${Auction.expired.name} = 0"
        val rs = Database.connection.createStatement().executeQuery("SELECT * FROM ${Auction.table} $where")
        return@callbackCatching rs.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun fetchAuction(id: Long) = callbackCatching {
        val query = "SELECT * FROM ${Auction.table} WHERE ${Auction.id.name}=$id"
        val response = Database.connection.createStatement().executeQuery(query)
        return@callbackCatching response.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun deleteAuction(key: Long): Boolean? = callbackCatching {
        val query = "DELETE FROM ${Auction.table} WHERE ${Auction.id.name}=${key}"
        return@callbackCatching Database.connection.prepareStatement(query).execute()
    }

    suspend fun countPlayerAuctions(player: Player) = callbackCatching {
        val query =
            "SELECT COUNT(*) FROM ${Auction.table} WHERE ${Auction.minecraftUuid.name}=\'${player.uuid}\' AND ${Auction.expired.name}=0"
        val response = Database.connection.createStatement().executeQuery(query)
        response.forEach { return@callbackCatching it.getInt(1)+1 }
        return@callbackCatching null
    }

}

