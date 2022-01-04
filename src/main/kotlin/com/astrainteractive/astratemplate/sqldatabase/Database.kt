package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.utils.AsyncTask
import kotlinx.coroutines.*
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * Database for plugin
 *
 * Not fully functional!
 */
class Database : AsyncTask {


    /**
     * Path for your plugin database
     *
     * Should be private
     */
    private val _dbPath = "${AstraAuctions.instance.dataFolder}${File.separator}data.db"


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

        launch(Dispatchers.IO) {
            connectDatabase()
            if (isInitialized) {
                Logger.log("База данных создана успешно", "Database")
                Repository.createAuctionTable()
            }
            else
                Logger.error("Не удалось создать базу данных","Database")
        }
    }

    public fun onDisable() {
        connection.close()
    }


}


