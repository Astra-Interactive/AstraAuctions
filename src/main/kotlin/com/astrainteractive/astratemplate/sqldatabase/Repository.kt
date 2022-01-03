package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.coroutineScope
import org.bukkit.entity.Player
import java.lang.Exception
import java.sql.ResultSet
import javax.xml.crypto.Data


/**
 * Repository with all SQL commands
 */
object Repository {
    /**
     * Return boolean of null if exception happened
     */
    suspend fun createAuctionTable(callback: Callback) =
        callbackCatching(callback) {
            if (!Database.isInitialized)
                throw Exception("Database not initialized")
            val res = Database.connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ${Auction.table} " +
                        "(" +
                        "${Auction.id.name} ${Auction.id.type} PRIMARY KEY AUTOINCREMENT, " +
                        "${Auction.discordId.name} ${Auction.discordId.type} NULL, " +
                        "${Auction.minecraftUuid.name} ${Auction.minecraftUuid.type} NOT NULL, " +
                        "${Auction.time.name} ${Auction.time.type} NOT NULL, " +
                        "${Auction.item.name} ${Auction.item.type} NOT NULL, " +
                        "${Auction.price.name} ${Auction.price.type} NOT NULL);"
            ).execute()
            callback.onSuccess(res)
        }


    suspend fun insertAuction(auction: Auction, callback: Callback) =
        callbackCatching(callback) {
            if (!Database.isInitialized)
                throw Exception("Database not initialized")
            var query = "INSERT INTO ${Auction.table} " +
                    "(${Auction.discordId.name}, ${Auction.minecraftUuid.name}, ${Auction.time.name}, ${Auction.item.name}, ${Auction.price.name}) " +
                    "VALUES(NULL, \'${auction.minecraftUuid}\', ${auction.time},?, ${auction.price} )"
            val statement = Database.connection.prepareStatement(query)
            statement.setBytes(1, auction.item)
            val result = statement.executeUpdate()
            callback.onSuccess(result)
        }

    suspend fun getAuctions(uuid: String? = null) = catching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val where = uuid?.let { "WHERE ${Auction.minecraftUuid.name}=${it}" } ?: ""
        val rs = Database.connection.createStatement().executeQuery("SELECT * FROM ${Auction.table} $where")
        return@catching rs.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun getAuction(id: Long) = catching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query = "SELECT * FROM ${Auction.table} WHERE ${Auction.id.name}=$id"
        val response = Database.connection.createStatement().executeQuery(query)
        return@catching response.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun removeAuction(key: Long, callback: Callback): Boolean? = callbackCatching(callback) {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query = "DELETE FROM ${Auction.table} WHERE ${Auction.id.name}=${key}"
        val response = Database.connection.prepareStatement(query).execute()
        callback.onSuccess(response)
        return@callbackCatching response
    }

    suspend fun countPlayerAuctions(player: Player) = catching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query =
            "SELECT COUNT(*) FROM ${Auction.table} WHERE ${Auction.minecraftUuid.name}=\'${player.uuid}\'"
        val response = Database.connection.createStatement().executeQuery(query)
        response.forEach { return@catching it.getInt(1) }
        return@catching null
    }

}

