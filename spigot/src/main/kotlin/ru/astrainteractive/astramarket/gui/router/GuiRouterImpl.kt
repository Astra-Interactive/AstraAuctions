package ru.astrainteractive.astramarket.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.players.PlayersGui
import ru.astrainteractive.astramarket.gui.slots.SlotsGui

class GuiRouterImpl(private val rootModule: RootModule) : GuiRouter {
    private val scope = rootModule.coreModule.scope.value
    private val dispatchers = rootModule.coreModule.dispatchers
    private val dependencies = AuctionGuiDependencies.Default(
        coreModule = rootModule.coreModule,
        marketDomainModule = rootModule.marketModule.marketDomainModule,
        bukkitCoreModule = rootModule.bukkitCoreModule,
        router = this@GuiRouterImpl
    )
    private val menuDrawerContext = MenuDrawerContext.Default(
        coreModule = rootModule.coreModule,
        marketDomainModule = rootModule.marketModule.marketDomainModule,
        bukkitCoreModule = rootModule.bukkitCoreModule,
        playersMarketModule = rootModule.playersMarketModule
    )

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.Slots -> {
                    SlotsGui(
                        player = route.player,
                        dependencies = dependencies,
                        menuDrawerContext = menuDrawerContext,
                        auctionComponent = rootModule.marketModule.createAuctionComponent(
                            playerUUID = route.player.uniqueId,
                            isExpired = route.isExpired,
                            targetPlayerUUID = route.targetPlayerUUID
                        )
                    )
                }

                is GuiRouter.Route.Players -> {
                    PlayersGui(
                        player = route.player,
                        dependencies = dependencies,
                        menuDrawerContext = menuDrawerContext,
                        playersMarketComponent = rootModule.playersMarketModule.createPlayersMarketComponent(
                            isExpired = route.isExpired
                        )
                    )
                }
            }
            withContext(rootModule.coreModule.dispatchers.Main) {
                gui.open()
            }
        }
    }
}
