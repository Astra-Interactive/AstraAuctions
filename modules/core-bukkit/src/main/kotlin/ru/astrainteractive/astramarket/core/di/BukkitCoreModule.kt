package ru.astrainteractive.astramarket.core.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.encoding.encoder.BukkitObjectEncoder
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astramarket.core.AstraMarketPlugin
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface BukkitCoreModule {
    val lifecycle: Lifecycle

    val plugin: Lateinit<AstraMarketPlugin>
    val encoder: Single<ObjectEncoder>

    val inventoryClickEventListener: Single<EventListener>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>

    class Default : BukkitCoreModule {

        override val plugin: Lateinit<AstraMarketPlugin> = Lateinit()

        override val encoder: Single<ObjectEncoder> = Single {
            BukkitObjectEncoder()
        }

        override val inventoryClickEventListener: Single<EventListener> = Single {
            DefaultInventoryClickEvent()
        }

        override val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer> = Reloadable {
            KyoriComponentSerializer.Legacy
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
}
