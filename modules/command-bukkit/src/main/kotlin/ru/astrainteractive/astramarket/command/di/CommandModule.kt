package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandRegistry
import ru.astrainteractive.astramarket.command.common.CommonCommandRegistry
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule

interface CommandModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        routerModule: RouterModule,
        marketViewModule: MarketViewModule
    ) : CommandModule {
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    CommonCommandRegistry(
                        plugin = bukkitCoreModule.plugin,
                        kyoriKrate = bukkitCoreModule.kyoriComponentSerializer,
                        pluginTranslationKrate = coreModule.pluginTranslationKrate
                    ).register()
                    AuctionCommandRegistry(
                        plugin = bukkitCoreModule.plugin,
                        kyoriKrate = bukkitCoreModule.kyoriComponentSerializer,
                        pluginTranslationKrate = coreModule.pluginTranslationKrate,
                        router = routerModule.router,
                        dispatchers = coreModule.dispatchers,
                        scope = coreModule.scope,
                        createAuctionUseCase = marketViewModule.marketViewDomainModule.createAuctionUseCase,
                        itemStackEncoder = bukkitCoreModule.itemStackEncoder
                    ).register()
                }
            )
        }
    }
}
