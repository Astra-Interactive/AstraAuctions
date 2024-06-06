package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface ButtonFactoryDependency {
    val sortTranslationMapping: AuctionSortTranslationMapping
    val config: PluginConfig
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val objectEncoder: ObjectEncoder

    class Default(
        coreModule: CoreModule,
        sharedDomainModule: SharedDomainModule,
        bukkitCoreModule: BukkitCoreModule,
    ) : ButtonFactoryDependency {
        override val sortTranslationMapping: AuctionSortTranslationMapping by Provider {
            sharedDomainModule.auctionSortTranslationMapping
        }
        override val config: PluginConfig by coreModule.config
        override val translation: Translation by coreModule.translation
        override val kyoriComponentSerializer: KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
        override val objectEncoder: ObjectEncoder by bukkitCoreModule.encoder
    }
}
