package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.api.AuctionsAPIImpl
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.utils.toDBConnection
import kotlinx.coroutines.runBlocking
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.orm.DefaultDatabase

object Modules {
    val configFileManager = module {
        SpigotFileManager(name = "config.yml")
    }
    val bukkitSerializer = module {
        Serializer(BukkitIOStreamProvider)
    }
    val translation = reloadable {
        Translation()
    }
    val configuration = reloadable {
        val configFileManager by configFileManager
        ConfigLoader.toClassOrDefault(configFileManager.configFile, AuctionConfig())
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