package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.api.entities.Auction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.database.DatabaseCore
import ru.astrainteractive.astralibs.database.isConnected
import ru.astrainteractive.astralibs.utils.catching
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
        PluginScope.launch(Dispatchers.IO) {
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


