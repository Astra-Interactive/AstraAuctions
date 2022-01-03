package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.utils.AsyncTask
import com.astrainteractive.astratemplate.sqldatabase.entities.Callback
import org.bukkit.ChatColor
import java.io.File
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import kotlinx.coroutines.*

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
            Repository.createAuctionTable(object : Callback() {
                override fun <T> onSuccess(result: T?) {
                    println("${ChatColor.AQUA}База данных создана успешно")
                }

                override fun onFailure(e: Exception) {
                    println("${ChatColor.RED}Не удалось создать базу данныъ ${e.message}")
                }

            })
        }
    }

    public fun onDisable() {
        connection.close()
    }


}


