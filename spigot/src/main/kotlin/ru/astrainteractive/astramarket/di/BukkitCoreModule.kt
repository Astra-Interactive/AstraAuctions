package ru.astrainteractive.astramarket.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Single

interface BukkitCoreModule {
    val lifecycle: Lifecycle

    val plugin: Lateinit<AstraMarket>
    val encoder: Single<Encoder>
    val bStats: Single<Metrics>

    val inventoryClickEventListener: Single<EventListener>
    val logger: Single<Logger>
    val stringSerializer: Single<KyoriComponentSerializer>
    val translationContext: BukkitTranslationContext
}
