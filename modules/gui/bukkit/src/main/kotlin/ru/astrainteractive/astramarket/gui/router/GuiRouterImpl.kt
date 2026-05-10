package ru.astrainteractive.astramarket.gui.router

import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.players.PlayersGui
import ru.astrainteractive.astramarket.gui.slots.SlotsGui
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class GuiRouterImpl(
    private val coreModule: CoreModule,
    private val marketViewModule: MarketViewModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val playersMarketViewModule: PlayersMarketViewModule
) : GuiRouter {

    private val buttonContext = ButtonContext.Default(
        coreModule = coreModule,
        marketViewDomainModule = marketViewModule.marketViewDomainModule,
        bukkitCoreModule = bukkitCoreModule,
        playersMarketViewModule = playersMarketViewModule
    )

    override fun navigate(route: GuiRouter.Route) {
        coreModule.ioScope.launch(coreModule.dispatchers.Main) {
            val menu = when (route) {
                is GuiRouter.Route.Slots -> {
                    SlotsGui(
                        inventoryOwner = route.inventoryOwner,
                        configKrate = coreModule.configKrate,
                        translationKrate = coreModule.pluginTranslationKrate,
                        dispatchers = coreModule.dispatchers,
                        router = this@GuiRouterImpl,
                        kyoriKrate = coreModule.kyoriKrate,
                        buttonContext = buttonContext,
                        auctionComponent = marketViewModule.createAuctionComponent(
                            playerUUID = route.inventoryOwner.uuid,
                            isExpired = route.isExpired,
                            targetPlayerUUID = route.targetPlayerUUID
                        )
                    )
                }

                is GuiRouter.Route.Players -> {
                    PlayersGui(
                        inventoryOwner = route.inventoryOwner,
                        configKrate = coreModule.configKrate,
                        translationKrate = coreModule.pluginTranslationKrate,
                        dispatchers = coreModule.dispatchers,
                        router = this@GuiRouterImpl,
                        kyoriKrate = coreModule.kyoriKrate,
                        buttonContext = buttonContext,
                        playersMarketComponent = playersMarketViewModule.createPlayersMarketComponent(
                            isExpired = route.isExpired
                        )
                    )
                }
            }
            route.inventoryOwner.tryCast<BukkitOnlineKPlayer>()
                ?.instance
                ?.openInventory(menu.inventory)
        }
    }
}
