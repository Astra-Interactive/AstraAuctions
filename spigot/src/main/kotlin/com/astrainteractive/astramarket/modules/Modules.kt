package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.api.AuctionsAPIImpl
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Files
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.utils.toDBConnection
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
import java.io.File

object Modules {
    val bukkitSerializer = module {
        Serializer(BukkitIOStreamProvider)
    }
    val translation = reloadable {
        Translation()
    }
    val configuration = reloadable {
        val config = EmpireSerializer.toClass<AuctionConfig>(Files.configFile)

        if (config == null){
            AstraMarket.instance.dataFolder.mkdirs()
            val oldConfig = File(Files.configFile.configFile.parentFile,"config.old.yml")
            Files.configFile.configFile.copyTo(oldConfig).createNewFile()

            Files.configFile.configFile.createNewFile()

            Files.configFile.configFile.writeText(Yaml.default.encodeToString(AuctionConfig()))
        }
        val realConfig = config?:AuctionConfig()

        return@reloadable realConfig
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