package ru.astrainteractive.astramarket.di.factory

import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable

internal class DatabaseFactory(
    private val dbConnection: DBConnection,
    private val dbSyntax: DBSyntax,
) {
    fun create(): Database {
        return runBlocking {
            val database = DefaultDatabase(dbConnection, dbSyntax)
            database.openConnection()
            AuctionTable.create(database)
            database
        }
    }
}
