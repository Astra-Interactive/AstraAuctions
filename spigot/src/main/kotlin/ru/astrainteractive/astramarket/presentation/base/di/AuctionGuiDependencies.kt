package ru.astrainteractive.astramarket.presentation.base.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface AuctionGuiDependencies {
    val config: PluginConfig
    val translation: Translation
    val dispatchers: KotlinDispatchers
    val sortTranslationMapping: AuctionSortTranslationMapping
    val objectEncoder: ObjectEncoder
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        coreModule: CoreModule,
        sharedDomainModule: SharedDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        override val router: GuiRouter
    ) : AuctionGuiDependencies {
        override val config: PluginConfig by coreModule.config
        override val translation: Translation by coreModule.translation
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val sortTranslationMapping: AuctionSortTranslationMapping by Provider {
            sharedDomainModule.auctionSortTranslationMapping
        }
        override val objectEncoder: ObjectEncoder by bukkitCoreModule.encoder
        override val kyoriComponentSerializer: KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
    }
}
