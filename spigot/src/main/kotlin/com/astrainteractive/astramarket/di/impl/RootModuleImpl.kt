package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.di.RootModule
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.api.AuctionsAPIImpl
import com.astrainteractive.astramarket.domain.entities.AuctionTable
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.util.toDBConnection
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

object RootModuleImpl : RootModule {
    override val plugin: Lateinit<AstraMarket> = Lateinit<AstraMarket>()
    override val configFileManager: Single<DefaultSpigotFileManager> = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, name = "config.yml")
    }
    override val bukkitSerializer: Single<Serializer> = Single {
        Serializer(BukkitIOStreamProvider)
    }
    override val translation: Reloadable<Translation> = Reloadable {
        Translation()
    }
    override val configuration: Reloadable<AuctionConfig> = Reloadable {
        val configFileManager by configFileManager
        ConfigLoader.toClassOrDefault(configFileManager.configFile, ::AuctionConfig)
    }
    override val database: Single<DefaultDatabase> = Single {
        val config by configuration
        runBlocking {
            val (dbconnection, dbsyntax) = config.connection.toDBConnection()
            val database = DefaultDatabase(dbconnection, dbsyntax)
            database.openConnection()
            AuctionTable.create(database)
            database
        }
    }
    override val auctionsApi: Single<AuctionsAPI> = Single {
        val database by database
        AuctionsAPIImpl(database) as AuctionsAPI
    }

    override val bStats: Single<Metrics> = Single {
        val plugin by plugin
        Metrics(plugin, 15771)
    }
    override val vaultEconomyProvider: Single<VaultEconomyProvider> = Single {
        VaultEconomyProvider()
    }

    @OptIn(UnsafeApi::class)
    override val scope: Single<AsyncComponent> = Single<AsyncComponent> {
        PluginScope
    }

    @OptIn(UnsafeApi::class)
    override val dispatchers: Single<BukkitDispatchers> = Single<BukkitDispatchers> {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val logger: Single<Logger> = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraMarket", plugin)
    }
}
