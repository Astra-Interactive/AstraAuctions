package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.api.AuctionsAPIImpl
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Files
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.utils.toDBConnection
import kotlinx.coroutines.runBlocking
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.astralibs.utils.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import ru.astrainteractive.astralibs.utils.toClass

object Modules {
    val bukkitSerializer = module {
        Serializer(BukkitIOStreamProvider)
    }
    val translation = reloadable {
        Translation()
    }
    val configuration = reloadable {
        EmpireSerializer.toClass<AuctionConfig>(Files.configFile) ?: AuctionConfig()
    }
    val database = module {
        val config by configuration
        runBlocking {
            val (dbconnection, dbsyntax) = config.connection.toDBConnection()
            val database = DefaultDatabase(dbconnection, dbsyntax)
            database.openConnection()
            AuctionTable.create(database)
            database
        }
    }
    val auctionsApi = module {
        val database by database
        AuctionsAPIImpl(database) as AuctionsAPI
    }

    val bStats = module {
        Metrics(AstraMarket.instance, 15771)
    }
}