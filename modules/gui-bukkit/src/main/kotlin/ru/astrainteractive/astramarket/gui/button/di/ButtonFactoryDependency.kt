package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.core.util.getValue
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping

internal interface ButtonFactoryDependency {
    val auctionSortTranslationMapping: AuctionSortTranslationMapping
    val playersSortTranslationMapping: PlayerSortTranslationMapping
    val config: PluginConfig
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val itemStackEncoder: ItemStackEncoder

    class Default(
        coreModule: CoreModule,
        marketViewDomainModule: MarketViewDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketViewModule: PlayersMarketViewModule
    ) : ButtonFactoryDependency {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by lazy {
            marketViewDomainModule.auctionSortTranslationMapping
        }
        override val playersSortTranslationMapping: PlayerSortTranslationMapping by lazy {
            playersMarketViewModule.playerSortTranslationMapping
        }
        override val config: PluginConfig by coreModule.configKrate
        override val translation: Translation by coreModule.translationKrate
        override val kyoriComponentSerializer: KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
    }
}
