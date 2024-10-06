package ru.astrainteractive.astramarket.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.di.MarketDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AuctionGuiDependencies {
    val config: PluginConfig
    val translation: Translation
    val dispatchers: KotlinDispatchers
    val sortTranslationMapping: AuctionSortTranslationMapping
    val itemStackEncoder: ItemStackEncoder
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        coreModule: CoreModule,
        marketDomainModule: MarketDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        override val router: GuiRouter
    ) : AuctionGuiDependencies {
        override val config: PluginConfig by coreModule.config
        override val translation: Translation by coreModule.translation
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val sortTranslationMapping: AuctionSortTranslationMapping by lazy {
            marketDomainModule.auctionSortTranslationMapping
        }
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
        override val kyoriComponentSerializer: KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
    }
}
