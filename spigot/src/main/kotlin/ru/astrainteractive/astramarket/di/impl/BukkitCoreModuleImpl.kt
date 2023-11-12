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
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class BukkitCoreModuleImpl : BukkitCoreModule {

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

    override val translationContext: BukkitTranslationContext by Provider {
        BukkitTranslationContext.Default { stringSerializer.value }
    }
}
