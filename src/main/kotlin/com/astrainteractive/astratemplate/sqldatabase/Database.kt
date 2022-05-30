package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.AuctionAPI
import kotlinx.coroutines.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database for plugin
 *
 * Not fully functional!
 */
class Database {


    /**
     * Path for your plugin database
     *
     * Should be private
     */
    private val _dbPath = "${AstraMarket.instance.dataFolder}${File.separator}data.db"


    /**
     * Connection for your database.
     *
     * You should call this object only from DatabaseQuerries
     * @See DatabaseQuerries
     */
    companion object {
        lateinit var connection: Connection
        val isInitialized: Boolean
            get() = this::connection.isInitialized && !connection.isClosed
        var isUpdated: Boolean = false

    }

    /**
     * Function for connecting to local database
     */
    private fun connectDatabase() =
        catching {
            connection = DriverManager.getConnection(("jdbc:sqlite:${_dbPath}"))
            return@catching true
        }


    fun onEnable() {

        AsyncHelper.launch(Dispatchers.IO) {
            connectDatabase()
            if (isInitialized) {
                Logger.log("Database created successfully", "Database")
                AuctionAPI.createAuctionTable()
                AuctionAPI.updateTable()
            }
            else
                Logger.error("Could not create the database","Database")
        }
    }

    public fun onDisable() {
        connection.close()
    }


}


