package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.command.api.registrar.PaperCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandExecutor
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.reload.ReloadCommandFactory
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
        private val commandRegistrar = PaperCommandRegistrarContext(
            mainScope = coreModule.mainScope,
            plugin = bukkitCoreModule.plugin
        )
        private val reloadCommandFactory = ReloadCommandFactory(
            plugin = bukkitCoreModule.plugin,
            translationKrate = coreModule.pluginTranslationKrate,
            kyori = bukkitCoreModule.kyoriComponentSerializer
        )
        private val auctionCommandFactory = AuctionCommandFactory(
            kyori = bukkitCoreModule.kyoriComponentSerializer,
            executor = AuctionCommandExecutor(
                router = routerModule.router,
                dispatchers = coreModule.dispatchers,
                createAuctionUseCase = marketViewModule.marketViewDomainModule.createAuctionUseCase,
                ioScope = coreModule.ioScope,
                itemStackEncoder = bukkitCoreModule.itemStackEncoder,
            )
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    commandRegistrar.registerWhenReady(reloadCommandFactory.create())
                    commandRegistrar.registerWhenReady(auctionCommandFactory.create())
                }
            )
        }
    }
}
