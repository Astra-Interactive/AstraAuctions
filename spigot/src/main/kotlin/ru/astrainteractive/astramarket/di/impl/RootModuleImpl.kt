package ru.astrainteractive.astramarket.di.impl

import org.bstats.bukkit.Metrics
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.economy.AnyEconomyProvider
import ru.astrainteractive.astralibs.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.di.DataModule
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.di.factory.DatabaseFactory
import ru.astrainteractive.astramarket.gui.di.AuctionGuiModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.toDBConnection
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class RootModuleImpl : RootModule {
    override val plugin: Lateinit<AstraMarket> = Lateinit<AstraMarket>()
    override val configFileManager: Single<DefaultSpigotFileManager> = Single {
        val plugin by plugin
        DefaultSpigotFileManager(plugin, name = "config.yml")
    }
    override val bukkitSerializer: Single<Serializer> = Single {
        Serializer(BukkitIOStreamProvider)
    }
    override val translation: Reloadable<Translation> = Reloadable {
        Translation(plugin.value)
    }
    override val configuration: Reloadable<AuctionConfig> = Reloadable {
        val configFileManager by configFileManager
        ConfigLoader().toClassOrDefault(configFileManager.configFile, ::AuctionConfig)
    }
    override val database: Single<Database> = Single {
        val config by configuration
        val (dbConnection, dbSyntax) = config.connection.toDBConnection()
        DatabaseFactory(
            dbConnection = dbConnection,
            dbSyntax = dbSyntax
        ).create()
    }

    override val dataModule: DataModule by Provider {
        DataModule.Default(
            database = database.value,
            dispatchers = dispatchers.value
        )
    }
    override val auctionGuiModule: AuctionGuiModule by Provider {
        AuctionGuiModule.Default(
            economyProvider = vaultEconomyProvider.value,
            config = configuration.value,
            translation = translation.value,
            serializer = bukkitSerializer.value,
            auctionApi = dataModule.auctionApi,
            dispatchers = dispatchers.value,
        )
    }

    override val bStats: Single<Metrics> = Single {
        val plugin by plugin
        Metrics(plugin, 15771)
    }
    override val vaultEconomyProvider = Single {
        AnyEconomyProvider(plugin.value)
    }

    @OptIn(UnsafeApi::class)
    override val scope: Single<AsyncComponent> = Single<AsyncComponent> {
        PluginScope
    }

    override val dispatchers: Single<BukkitDispatchers> = Single<BukkitDispatchers> {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val logger: Single<Logger> = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraMarket", plugin)
    }
}
