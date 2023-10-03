package ru.astrainteractive.astramarket.di.impl

import kotlinx.serialization.encodeToString
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.AnyEconomyProvider
import ru.astrainteractive.astralibs.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.permission.BukkitPermissionManager
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.di.DataModule
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.di.factory.DatabaseFactory
import ru.astrainteractive.astramarket.domain.di.BukkitDomainModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.di.AuctionGuiModule
import ru.astrainteractive.astramarket.util.ConnectionExt.toDBConnection
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class RootModuleImpl : RootModule {
    override val plugin: Lateinit<AstraMarket> = Lateinit<AstraMarket>()
    override val encoder: Single<Encoder> = Single {
        Encoder(BukkitIOStreamProvider)
    }
    override val translation: Reloadable<Translation> = Reloadable {
        val fileManager = DefaultSpigotFileManager(plugin.value, name = "translations.yml")
        val serializer = YamlSerializer()
        runCatching {
            serializer.unsafeParse<Translation>(fileManager.configFile)
        }.onSuccess {
            fileManager.configFile.writeText(serializer.yaml.encodeToString(it))
        }.getOrNull() ?: Translation()
    }
    override val configuration: Reloadable<AuctionConfig> = Reloadable {
        val fileManager = DefaultSpigotFileManager(plugin.value, name = "config.yml")
        val serializer = YamlSerializer()
        runCatching {
            serializer.unsafeParse<AuctionConfig>(fileManager.configFile)
        }.onSuccess {
            fileManager.configFile.writeText(serializer.yaml.encodeToString(it))
        }.getOrNull() ?: AuctionConfig()
    }
    override val database: Single<Database> = Single {
        val config by configuration
        val (dbConnection, dbSyntax) = config.connection.toDBConnection()
        DatabaseFactory(
            dbConnection = dbConnection,
            dbSyntax = dbSyntax
        ).create()
    }
    override val permissionManager: Single<PermissionManager> = Single {
        BukkitPermissionManager()
    }

    override val bStats: Single<Metrics> = Single {
        val plugin by plugin
        Metrics(plugin, 15771)
    }
    override val economyProvider = Single {
        AnyEconomyProvider(plugin.value)
    }

    override val scope: Single<AsyncComponent> = Single<AsyncComponent> {
        AsyncComponent.Default()
    }
    override val inventoryClickEventListener: Single<EventListener> = Single {
        DefaultInventoryClickEvent()
    }

    override val stringSerializer: Single<KyoriComponentSerializer> = Single {
        KyoriComponentSerializer.Legacy
    }

    override val dispatchers: Single<BukkitDispatchers> = Single<BukkitDispatchers> {
        val plugin by plugin
        DefaultBukkitDispatchers(plugin)
    }
    override val logger: Single<Logger> = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraMarket", plugin)
    }

    // Modules
    override val dataModule: DataModule by Provider {
        DataModule.Default(
            database = database.value,
            dispatchers = dispatchers.value
        )
    }
    override val bukkitDomainModule: BukkitDomainModule by Provider {
        BukkitDomainModule.Default(
            dataModule = dataModule,
            encoder = encoder.value,
            stringSerializer = stringSerializer.value,
            permissionManager = permissionManager.value
        )
    }
    override val sharedDomainModule: SharedDomainModule by Provider {
        SharedDomainModule.Default(
            translation = translation.value,
            configuration = configuration.value,
            economyProvider = economyProvider.value,
            auctionsRepository = bukkitDomainModule.auctionsRepository,
            playerInteraction = bukkitDomainModule.playerInteraction
        )
    }
    override val auctionGuiModule: AuctionGuiModule by Provider {
        AuctionGuiModule.Default(
            config = configuration.value,
            translation = translation.value,
            serializer = encoder.value,
            auctionApi = dataModule.auctionApi,
            dispatchers = dispatchers.value,
            stringSerializer = stringSerializer.value,
            bukkitDomainModule = bukkitDomainModule,
            sharedDomainModule = sharedDomainModule
        )
    }
}
