package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandExecutor
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
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
        marketViewModule: MarketViewModule,
        private val commandRegistrarContext: CommandRegistrarContext,
        private val multiplatformCommand: MultiplatformCommand<*>,
    ) : CommandModule {
        private val errorHandler = BrigadierErrorHandler(
            kyoriComponentSerializer = bukkitCoreModule.kyoriComponentSerializer,
            translationKrate = coreModule.pluginTranslationKrate,
            multiplatformCommand = multiplatformCommand
        )
        private val reloadCommandFactory = ReloadCommandFactory(
            plugin = bukkitCoreModule.plugin,
            translationKrate = coreModule.pluginTranslationKrate,
            kyori = bukkitCoreModule.kyoriComponentSerializer,
            errorHandler = errorHandler,
            multiplatformCommand = multiplatformCommand
        )
        private val auctionCommandFactory = AuctionCommandFactory(
            kyoriKrate = bukkitCoreModule.kyoriComponentSerializer,
            errorHandler = errorHandler,
            executor = AuctionCommandExecutor(
                router = routerModule.router,
                dispatchers = coreModule.dispatchers,
                createAuctionUseCase = marketViewModule.marketViewDomainModule.createAuctionUseCase,
                ioScope = coreModule.ioScope,
                itemStackEncoder = bukkitCoreModule.itemStackEncoder,
            ),
            multiplatformCommand = multiplatformCommand,
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    buildList {
                        addAll(auctionCommandFactory.create())
                        add(reloadCommandFactory.create())
                    }.onEach(commandRegistrarContext::registerWhenReady)
                }
            )
        }
    }
}
