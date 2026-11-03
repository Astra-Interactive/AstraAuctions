@file:Suppress("FieldDependencyConstruction")

package ru.astrainteractive.astramarket.gui.bukkit.router

import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.api.market.di.MarketViewModule
import ru.astrainteractive.astramarket.gui.api.player.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.gui.api.router.api.GuiRouter
import ru.astrainteractive.astramarket.gui.bukkit.context.api.ButtonContext
import ru.astrainteractive.astramarket.gui.bukkit.market.gui.SlotsGui
import ru.astrainteractive.astramarket.gui.bukkit.player.gui.PlayersGui
import ru.astrainteractive.klibs.mikro.core.util.tryCast
import java.util.Locale

internal class BukkitGuiRouter(
    private val coreModule: CoreModule,
    private val marketViewModule: MarketViewModule,
    private val objectEncoder: ObjectEncoder,
    private val playersMarketViewModule: PlayersMarketViewModule
) : GuiRouter {

    private fun createButtonContext(locale: Locale): ButtonContext {
        return ButtonContext.Default(
            coreModule = coreModule,
            objectEncoder = objectEncoder,
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
