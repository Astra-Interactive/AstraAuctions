package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping
import ru.astrainteractive.klibs.kstorage.util.getValue

internal interface ButtonContext : KyoriComponentSerializer, Logger {
    val auctionSortTranslationMapping: AuctionSortTranslationMapping
    val playersSortTranslationMapping: PlayerSortTranslationMapping
    val config: PluginConfig
    val pluginTranslation: PluginTranslation
    val itemStackEncoder: ItemStackEncoder

    class Default(
        coreModule: CoreModule,
        marketViewDomainModule: MarketViewDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketViewModule: PlayersMarketViewModule
    ) : ButtonContext,
        Logger by JUtiltLogger("AstraMarket-ButtonContext"),
        KyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer.cachedValue {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by lazy {
            marketViewDomainModule.auctionSortTranslationMapping
        }
        override val playersSortTranslationMapping: PlayerSortTranslationMapping by lazy {
            playersMarketViewModule.playerSortTranslationMapping
        }
        override val config: PluginConfig by coreModule.configKrate
        override val pluginTranslation: PluginTranslation by coreModule.pluginTranslationKrate
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
    }
}
