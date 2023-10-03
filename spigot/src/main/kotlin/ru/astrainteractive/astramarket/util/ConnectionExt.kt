@file:Suppress("Filename")

package ru.astrainteractive.astramarket.util

import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astramarket.plugin.AuctionConfig

object ConnectionExt {
    fun AuctionConfig.Connection.toDBConnection(): Pair<DBConnection, DBSyntax> {
        val mysql = this.mysql
        val sqlite = this.sqlite
        val sqliteConnection = DBConnection.SQLite("dbv2_auction.db") to DBSyntax.SQLite
        if (sqlite || mysql == null) return sqliteConnection
        return DBConnection.MySQL(
            database = mysql.database,
            ip = mysql.ip,
            port = mysql.port,
            username = mysql.username,
            password = mysql.password,
            sessionVariables = mysql.sessionVariables.toTypedArray()
        ) to DBSyntax.MySQL
    }
}
