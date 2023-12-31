package ru.astrainteractive.astramarket.di.impl

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.encoding.BukkitIOStreamProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.util.buildWithSpigot
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

class BukkitCoreModuleImpl : BukkitCoreModule {

    override val plugin: Lateinit<AstraMarket> = Lateinit<AstraMarket>()

    override val encoder: Single<Encoder> = Single {
        Encoder(BukkitIOStreamProvider)
    }

    override val inventoryClickEventListener: Single<EventListener> = Single {
        DefaultInventoryClickEvent()
    }

    override val stringSerializer: Single<KyoriComponentSerializer> = Single {
        KyoriComponentSerializer.Legacy
    }

    override val logger: Single<Logger> = Single {
        val plugin by plugin
        Logger.buildWithSpigot("AstraMarket", plugin)
    }

    override val translationContext: BukkitTranslationContext by Provider {
        BukkitTranslationContext.Default { stringSerializer.value }
    }

    private val bStatsFactory = Factory {
        val plugin by plugin
        Metrics(plugin, 15771)
    }

    override val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                bStatsFactory.create()
                inventoryClickEventListener.value.onEnable(plugin.value)
            },
            onDisable = {
                inventoryClickEventListener.value.onEnable(plugin.value)
            }
        )
    }
}
