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
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.astralibs.utils.buildWithSpigot

object Modules {
    val plugin = Lateinit<AstraMarket>()
    val configFileManager = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, name = "config.yml")
    }
    val bukkitSerializer = Single {
        Serializer(BukkitIOStreamProvider)
    }
    val translation = Reloadable {
        Translation()
    }
    val configuration = Reloadable {
        val configFileManager by configFileManager
        ConfigLoader.toClassOrDefault(configFileManager.configFile, ::AuctionConfig)
    }
    val database = Single {
        val config by configuration
        runBlocking {
            val (dbconnection, dbsyntax) = config.connection.toDBConnection()
            val database = DefaultDatabase(dbconnection, dbsyntax)
            database.openConnection()
            AuctionTable.create(database)
            database
        }
    }
    val auctionsApi = Single {
        val database by database
        AuctionsAPIImpl(database) as AuctionsAPI
    }

    val bStats = Single {
        val plugin by plugin
        Metrics(plugin, 15771)
    }
    val vaultEconomyProvider = Single {
        VaultEconomyProvider()
    }

    @OptIn(UnsafeApi::class)
    val scope = Single<AsyncComponent> {
        PluginScope
    }

    @OptIn(UnsafeApi::class)
    val dispatchers = Single<BukkitDispatchers> {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    val logger = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraMarket", plugin)
    }
}
