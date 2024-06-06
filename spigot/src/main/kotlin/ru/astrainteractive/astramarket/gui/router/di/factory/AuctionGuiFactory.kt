package ru.astrainteractive.astramarket.gui.router.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.gui.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.gui.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionComponentFactory
import ru.astrainteractive.klibs.kdi.Factory

@Suppress("LongParameterList")
class AuctionGuiFactory(
    private val player: Player,
    private val isExpired: Boolean,
    private val coreModule: CoreModule,
    private val apiMarketModule: ApiMarketModule,
    private val sharedDomainModule: SharedDomainModule,
    private val bukkitCoreModule: BukkitCoreModule,
    private val router: GuiRouter
) : Factory<AbstractAuctionGui> {
    override fun create(): AbstractAuctionGui {
        val auctionComponent = AuctionComponentFactory(
            playerUUID = player.uniqueId,
            isExpired = isExpired,
            coreModule = coreModule,
            apiMarketModule = apiMarketModule,
            sharedDomainModule = sharedDomainModule,
            sharedDataModule = sharedDomainModule.sharedDataModule
        ).create()
        val dependencies = AuctionGuiDependencies.Default(
            coreModule = coreModule,
            sharedDomainModule = sharedDomainModule,
            bukkitCoreModule = bukkitCoreModule,
            router = router
        )
        val menuDrawerContext = MenuDrawerContext.Default(
            coreModule = coreModule,
            sharedDomainModule = sharedDomainModule,
            bukkitCoreModule = bukkitCoreModule,
        )
        return if (isExpired) {
            ExpiredAuctionGui(
                player = player,
                dependencies = dependencies,
                auctionComponent = auctionComponent,
                menuDrawerContext = menuDrawerContext
            )
        } else {
            ru.astrainteractive.astramarket.gui.auctions.AuctionGui(
                player = player,
                auctionComponent = auctionComponent,
                dependencies = dependencies,
                menuDrawerContext = menuDrawerContext
            )
        }
    }
}
