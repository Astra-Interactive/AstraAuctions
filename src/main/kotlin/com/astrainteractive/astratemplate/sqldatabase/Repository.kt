package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import org.bukkit.entity.Player


/**
 * Repository with all SQL commands
 */
object Repository {
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
                        "${Auction.price.name} ${Auction.price.type} NOT NULL);"
            ).execute()
        }


    suspend fun insertAuction(auction: Auction) =
        callbackCatching {
            if (!Database.isInitialized)
                throw Exception("Database not initialized")
            var query = "INSERT INTO ${Auction.table} " +
                    "(${Auction.discordId.name}, ${Auction.minecraftUuid.name}, ${Auction.time.name}, ${Auction.item.name}, ${Auction.price.name}) " +
                    "VALUES(NULL, \'${auction.minecraftUuid}\', ${auction.time},?, ${auction.price} )"
            val statement = Database.connection.prepareStatement(query)
            statement.setBytes(1, auction.item)
            return@callbackCatching statement.executeUpdate()
        }

    suspend fun getAuctions(uuid: String? = null) = callbackCatching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val where = uuid?.let { "WHERE ${Auction.minecraftUuid.name}=${it}" } ?: ""
        val rs = Database.connection.createStatement().executeQuery("SELECT * FROM ${Auction.table} $where")
        return@callbackCatching rs.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun getAuction(id: Long) = callbackCatching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query = "SELECT * FROM ${Auction.table} WHERE ${Auction.id.name}=$id"
        val response = Database.connection.createStatement().executeQuery(query)
        return@callbackCatching response.mapNotNull { Auction.fromResultSet(it) }
    }

    suspend fun removeAuction(key: Long): Boolean? = callbackCatching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query = "DELETE FROM ${Auction.table} WHERE ${Auction.id.name}=${key}"
        return@callbackCatching Database.connection.prepareStatement(query).execute()
    }

    suspend fun countPlayerAuctions(player: Player) = callbackCatching {
        if (!Database.isInitialized)
            throw Exception("Database not initialized")
        val query =
            "SELECT COUNT(*) FROM ${Auction.table} WHERE ${Auction.minecraftUuid.name}=\'${player.uuid}\'"
        val response = Database.connection.createStatement().executeQuery(query)
        response.forEach { return@callbackCatching it.getInt(1) }
        return@callbackCatching null
    }

}

