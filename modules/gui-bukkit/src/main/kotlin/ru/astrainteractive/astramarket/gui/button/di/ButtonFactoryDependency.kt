package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.di.MarketDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

internal interface ButtonFactoryDependency {
    val auctionSortTranslationMapping: AuctionSortTranslationMapping
    val playersSortTranslationMapping: PlayerSortTranslationMapping
    val config: PluginConfig
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer
    val itemStackEncoder: ItemStackEncoder

    class Default(
        coreModule: CoreModule,
        marketDomainModule: MarketDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketModule: PlayersMarketModule
    ) : ButtonFactoryDependency {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by Provider {
            marketDomainModule.auctionSortTranslationMapping
        }
        override val playersSortTranslationMapping: PlayerSortTranslationMapping by Provider {
            playersMarketModule.playerSortTranslationMapping
        }
        override val config: PluginConfig by coreModule.config
        override val translation: Translation by coreModule.translation
        override val kyoriComponentSerializer: KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
    }
}
