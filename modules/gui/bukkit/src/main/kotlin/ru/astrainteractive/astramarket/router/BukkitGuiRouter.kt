package ru.astrainteractive.astramarket.router

import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astramarket.context.ButtonContext
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.market.gui.SlotsGui
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.players.gui.PlayersGui
import ru.astrainteractive.astramarket.router.api.GuiRouter
import ru.astrainteractive.klibs.mikro.core.util.tryCast
import java.util.Locale

internal class BukkitGuiRouter(
    private val coreModule: CoreModule,
    private val marketViewModule: MarketViewModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val playersMarketViewModule: PlayersMarketViewModule
) : GuiRouter {

    private fun createButtonContext(locale: Locale): ButtonContext {
        return ButtonContext.Default(
            coreModule = coreModule,
            bukkitCoreModule = bukkitCoreModule,
            locale = locale
        )
    }

    override fun navigate(route: GuiRouter.Route) {
        coreModule.ioScope.launch(coreModule.dispatchers.Main) {
            val buttonContext = createButtonContext(route.inventoryOwner.locale)
            val menu = when (route) {
                is GuiRouter.Route.Slots -> {
                    SlotsGui(
                        inventoryOwner = route.inventoryOwner,
                        configKrate = coreModule.configKrate,
                        translationKrate = coreModule.pluginTranslationKrate,
                        dispatchers = coreModule.dispatchers,
                        router = this@BukkitGuiRouter,
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
                        router = this@BukkitGuiRouter,
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
