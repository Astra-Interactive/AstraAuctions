package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping
import ru.astrainteractive.klibs.kstorage.util.getValue

internal interface ButtonContext : KyoriComponentSerializer {
    val auctionSortTranslationMapping: AuctionSortTranslationMapping
    val playersSortTranslationMapping: PlayerSortTranslationMapping
    val config: PluginConfig
    val translation: Translation
    val itemStackEncoder: ItemStackEncoder

    class Default(
        coreModule: CoreModule,
        marketViewDomainModule: MarketViewDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketViewModule: PlayersMarketViewModule
    ) : ButtonContext,
        KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer.cachedValue {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by lazy {
            marketViewDomainModule.auctionSortTranslationMapping
        }
        override val playersSortTranslationMapping: PlayerSortTranslationMapping by lazy {
            playersMarketViewModule.playerSortTranslationMapping
        }
        override val config: PluginConfig by coreModule.configKrate
        override val translation: Translation by coreModule.translationKrate
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
    }
}
