package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astramarket.domain.DataSource
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.database_v2.Database
import ru.astrainteractive.astralibs.di.IModule

object DatabaseModule : IModule<Database>() {
    override fun initializer(): Database = runBlocking {
        val database = Database()
        database.openConnection("jdbc:sqlite:dbv2_auction.db", "org.sqlite.JDBC")
        AuctionTable.create()
        database
    }
}