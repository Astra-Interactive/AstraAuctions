package ru.astrainteractive.astramarket.presentation.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.presentation.router.di.factory.AuctionComponentFactory
import ru.astrainteractive.astramarket.presentation.router.di.factory.AuctionGuiFactory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class GuiRouterImpl(private val rootModule: RootModule) : GuiRouter {
    private val scope = rootModule.bukkitCoreModule.scope.value
    private val dispatchers = rootModule.bukkitCoreModule.dispatchers.value
    private val auctionComponentFactory: AuctionComponentFactory by Provider {
        AuctionComponentFactory(
            dispatchers = rootModule.bukkitCoreModule.dispatchers.value,
            marketApi = rootModule.dataModule.auctionApi,
            sharedDomainModule = rootModule.sharedDomainModule,
            config = rootModule.bukkitCoreModule.configuration.value,
            playerInteractionBridge = rootModule.sharedDomainModule.sharedDataModule.playerInteractionBridge,
            sortAuctionsUseCase = rootModule.sharedDomainModule.platformSharedDomainModule.sortAuctionsUseCase
        )
    }

    private val auctionGuiFactory: AuctionGuiFactory by Provider {
        AuctionGuiFactory(
            auctionComponentFactory = auctionComponentFactory,
            config = rootModule.bukkitCoreModule.configuration.value,
            translation = rootModule.bukkitCoreModule.translation.value,
            dispatchers = rootModule.bukkitCoreModule.dispatchers.value,
            auctionSortTranslationMapping = rootModule.sharedDomainModule.auctionSortTranslationMapping,
            serializer = rootModule.bukkitCoreModule.encoder.value,
            stringSerializer = rootModule.bukkitCoreModule.stringSerializer.value
        )
    }

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = when (route) {
                is GuiRouter.Route.Auctions -> {
                    auctionGuiFactory.create(
                        player = route.player,
                        isExpired = false
                    )
                }

                is GuiRouter.Route.ExpiredAuctions -> {
                    auctionGuiFactory.create(
                        player = route.player,
                        isExpired = true
                    )
                }
            }
            withContext(rootModule.bukkitCoreModule.dispatchers.value.BukkitMain) {
                gui.open()
            }
        }
    }
}
