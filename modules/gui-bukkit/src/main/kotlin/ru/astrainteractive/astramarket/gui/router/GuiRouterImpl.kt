package ru.astrainteractive.astramarket.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.players.PlayersGui
import ru.astrainteractive.astramarket.gui.slots.SlotsGui
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketModule

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val marketModule: MarketModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val playersMarketModule: PlayersMarketModule
) : GuiRouter {
    private val scope = coreModule.scope.value
    private val dispatchers = coreModule.dispatchers
    private val dependencies = AuctionGuiDependencies.Default(
        coreModule = coreModule,
        marketDomainModule = marketModule.marketDomainModule,
        bukkitCoreModule = bukkitCoreModule,
        router = this@GuiRouterImpl
    )
    private val menuDrawerContext = MenuDrawerContext.Default(
        coreModule = coreModule,
        marketDomainModule = marketModule.marketDomainModule,
        bukkitCoreModule = bukkitCoreModule,
        playersMarketModule = playersMarketModule
    )

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.Slots -> {
                    SlotsGui(
                        player = route.player,
                        dependencies = dependencies,
                        menuDrawerContext = menuDrawerContext,
                        auctionComponent = marketModule.createAuctionComponent(
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
                        playersMarketComponent = playersMarketModule.createPlayersMarketComponent(
                            isExpired = route.isExpired
                        )
                    )
                }
            }
            withContext(coreModule.dispatchers.Main) {
                gui.open()
            }
        }
    }
}
