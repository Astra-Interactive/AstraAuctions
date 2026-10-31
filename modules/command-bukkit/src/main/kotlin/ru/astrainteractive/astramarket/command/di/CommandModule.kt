package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.api.CommandExceptionHandler
import ru.astrainteractive.astramarket.command.auction.command.AuctionCommandExecutor
import ru.astrainteractive.astramarket.command.auction.command.AuctionLiteralArgumentBuilder
import ru.astrainteractive.astramarket.command.reload.command.ReloadLiteralArgumentBuilder
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.api.market.di.MarketViewModule
import ru.astrainteractive.astramarket.gui.api.router.di.RouterModule

interface CommandModule {
    val lifecycle: Lifecycle

    class Default(
        private val coreModule: CoreModule,
        objectEncoder: ObjectEncoder,
        bukkitRouterModule: RouterModule,
        marketViewModule: MarketViewModule,
        private val commandRegistrarContext: CommandRegistrarContext,
    ) : CommandModule {
        private val commandExceptionHandler = CommandExceptionHandler(
            multiplatformCommand = coreModule.multiplatformCommand,
            translationKrate = coreModule.pluginTranslationKrate
        )
        private val reloadLiteralArgumentBuilder = ReloadLiteralArgumentBuilder(
            lifecyclePlugin = coreModule.lifecyclePlugin,
            translationKrate = coreModule.pluginTranslationKrate,
            commandExceptionHandler = commandExceptionHandler,
            multiplatformCommand = coreModule.multiplatformCommand
        )
        private val auctionLiteralArgumentBuilder = AuctionLiteralArgumentBuilder(
            commandExceptionHandler = commandExceptionHandler,
            executor = AuctionCommandExecutor(
                router = bukkitRouterModule.router,
                dispatchers = coreModule.dispatchers,
                createAuctionUseCase = marketViewModule.marketViewDomainModule.createAuctionUseCase,
                ioScope = coreModule.ioScope,
                objectEncoder = objectEncoder,
                clock = coreModule.clock
            ),
            multiplatformCommand = coreModule.multiplatformCommand,
        )

        private val nodes = buildList {
            addAll(auctionLiteralArgumentBuilder.create())
            add(reloadLiteralArgumentBuilder.create())
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    commandRegistrarContext.registerWhenReady(nodes, coreModule.unconfinedScope)
                }
            )
        }
    }
}
