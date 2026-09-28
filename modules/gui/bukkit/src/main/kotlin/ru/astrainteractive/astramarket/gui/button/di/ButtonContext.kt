package ru.astrainteractive.astramarket.gui.button.di

import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.di.MarketViewDomainModule
import ru.astrainteractive.astramarket.market.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.Locale

/** What the buttons of one menu need, including the language of the player who opened the menu. */
internal interface ButtonContext : Logger {
    val auctionSortTranslationMapping: AuctionSortTranslationMapping
    val playersSortTranslationMapping: PlayerSortTranslationMapping
    val config: PluginConfig
    val pluginTranslation: PluginTranslation
    val itemStackEncoder: ItemStackEncoder
    val locale: Locale

    class Default(
        coreModule: CoreModule,
        marketViewDomainModule: MarketViewDomainModule,
        bukkitCoreModule: BukkitCoreModule,
        playersMarketViewModule: PlayersMarketViewModule,
        override val locale: Locale
    ) : ButtonContext,
        Logger by JUtiltLogger("AstraMarket-ButtonContext").withoutParentHandlers() {
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
