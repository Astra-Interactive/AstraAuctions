package ru.astrainteractive.astramarket.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.players.PlayersGui
import ru.astrainteractive.astramarket.gui.slots.SlotsGui
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val marketViewModule: MarketViewModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val playersMarketViewModule: PlayersMarketViewModule
) : GuiRouter {
    private val dependencies = AuctionGuiDependencies.Default(
        coreModule = coreModule,
        marketViewDomainModule = marketViewModule.marketViewDomainModule,
        bukkitCoreModule = bukkitCoreModule,
        router = this@GuiRouterImpl
    )
    private val buttonContext = ButtonContext.Default(
        coreModule = coreModule,
        marketViewDomainModule = marketViewModule.marketViewDomainModule,
        bukkitCoreModule = bukkitCoreModule,
        playersMarketViewModule = playersMarketViewModule
    )

    override fun navigate(route: GuiRouter.Route) {
        coreModule.scope.launch {
            val gui = when (route) {
                is GuiRouter.Route.Slots -> {
                    SlotsGui(
                        player = route.player,
                        dependencies = dependencies,
                        buttonContext = buttonContext,
                        auctionComponent = marketViewModule.createAuctionComponent(
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
                        buttonContext = buttonContext,
                        playersMarketComponent = playersMarketViewModule.createPlayersMarketComponent(
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
