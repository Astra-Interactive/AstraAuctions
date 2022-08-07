package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.database.DatabaseCore
import com.astrainteractive.astralibs.database.isConnected
import com.astrainteractive.astralibs.utils.catching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


/**
 * Database for plugin
 *
 * Not fully functional!
 */
class Database(filePath: String = "${AstraLibs.instance.dataFolder}${File.separator}data.db") : DatabaseCore() {

    override val connectionBuilder: () -> Connection? = {
        catching { DriverManager.getConnection(("jdbc:sqlite:${filePath}")) }
    }


    override suspend fun onEnable() {
        if (connection.isConnected)
            Logger.log("Database created successfully", "Database")
        else {
            Logger.error("Could not create the database", "Database")
            return
        }
        AsyncHelper.launch(Dispatchers.IO) {
            createTable<Auction>()
        }
    }

    companion object {
        var instance: Database? = null
            private set
    }

    init {
        instance = this
    }
}


