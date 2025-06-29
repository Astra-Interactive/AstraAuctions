package ru.astrainteractive.astramarket.gui.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AuctionGuiDependencies {
    val config: PluginConfig
    val pluginTranslation: PluginTranslation
    val dispatchers: KotlinDispatchers
    val sortTranslationMapping: AuctionSortTranslationMapping
    val itemStackEncoder: ItemStackEncoder
    val kyoriComponentSerializer: KyoriComponentSerializer
    val router: GuiRouter

    class Default(
        coreModule: CoreModule,
        marketViewDomainModule: MarketViewDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        override val router: GuiRouter
    ) : AuctionGuiDependencies {
        override val config: PluginConfig by coreModule.configKrate
        override val pluginTranslation: PluginTranslation by coreModule.pluginTranslationKrate
        override val kyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer

        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val sortTranslationMapping = marketViewDomainModule.auctionSortTranslationMapping
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
    }
}
