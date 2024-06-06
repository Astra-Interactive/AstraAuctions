package ru.astrainteractive.astramarket.gui.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.gui.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.gui.router.di.factory.AuctionGuiFactory
import ru.astrainteractive.klibs.kdi.getValue

class GuiRouterImpl(private val rootModule: RootModule) : GuiRouter {
    private val scope = rootModule.coreModule.scope.value
    private val dispatchers = rootModule.coreModule.dispatchers

    private fun createGui(player: Player, isExpired: Boolean): AbstractAuctionGui {
        return AuctionGuiFactory(
            player = player,
            isExpired = isExpired,
            coreModule = rootModule.coreModule,
            apiMarketModule = rootModule.apiMarketModule,
            bukkitCoreModule = rootModule.bukkitCoreModule,
            router = this,
            sharedDomainModule = rootModule.sharedDomainModule
        ).create()
    }

    override fun navigate(route: GuiRouter.Route) {
        scope.launch(dispatchers.IO) {
            val gui = when (route) {
                is GuiRouter.Route.Auctions -> createGui(
                    player = route.player,
                    isExpired = false
                )

                is GuiRouter.Route.ExpiredAuctions -> createGui(
                    player = route.player,
                    isExpired = true
                )
            }
            withContext(rootModule.coreModule.dispatchers.Main) {
                gui.open()
            }
        }
    }
}
