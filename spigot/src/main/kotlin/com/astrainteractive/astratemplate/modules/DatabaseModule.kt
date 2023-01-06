package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astramarket.domain.entities.AuctionTable
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.Database

object DatabaseModule : IModule<Database>() {
    override fun initializer(): Database = runBlocking {
        val database = Database()
        database.openConnection("dbv2_auction.db", DBConnection.SQLite)
        AuctionTable.create()
        database
    }
}