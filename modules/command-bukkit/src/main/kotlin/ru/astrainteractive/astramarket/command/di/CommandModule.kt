package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandExecutor
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.errorhandler.BrigadierErrorHandler
import ru.astrainteractive.astramarket.command.reload.ReloadLiteralArgumentBuilder
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule

interface CommandModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        bukkitRouterModule: RouterModule,
        marketViewModule: MarketViewModule,
        private val commandRegistrarContext: CommandRegistrarContext,
        private val multiplatformCommand: MultiplatformCommand,
    ) : CommandModule {
        private val errorHandler = BrigadierErrorHandler(
            kyoriComponentSerializer = coreModule.kyoriKrate,
            translationKrate = coreModule.pluginTranslationKrate,
            multiplatformCommand = multiplatformCommand
        )
        private val reloadLiteralArgumentBuilder = ReloadLiteralArgumentBuilder(
            lifecyclePlugin = coreModule.lifecyclePlugin,
            translationKrate = coreModule.pluginTranslationKrate,
            kyori = coreModule.kyoriKrate,
            errorHandler = errorHandler,
            multiplatformCommand = multiplatformCommand
        )
        private val auctionCommandFactory = AuctionCommandFactory(
            kyoriKrate = coreModule.kyoriKrate,
            errorHandler = errorHandler,
            executor = AuctionCommandExecutor(
                router = bukkitRouterModule.router,
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
                        add(reloadLiteralArgumentBuilder.create())
                    }.onEach(commandRegistrarContext::registerWhenReady)
                }
            )
        }
    }
}
