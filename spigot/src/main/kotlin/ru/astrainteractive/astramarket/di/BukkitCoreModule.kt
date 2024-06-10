package ru.astrainteractive.astramarket.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface BukkitCoreModule {
    val lifecycle: Lifecycle

    val plugin: Lateinit<AstraMarket>
    val encoder: Single<ObjectEncoder>

    val inventoryClickEventListener: Single<EventListener>
    val kyoriComponentSerializer: Reloadable<KyoriComponentSerializer>
}
