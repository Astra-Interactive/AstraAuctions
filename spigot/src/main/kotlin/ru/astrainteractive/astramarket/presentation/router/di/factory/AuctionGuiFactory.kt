package ru.astrainteractive.astramarket.presentation.router.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.presentation.auctions.AuctionGui
import ru.astrainteractive.astramarket.presentation.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionComponentFactory
import ru.astrainteractive.astramarket.presentation.expired.ExpiredAuctionGui
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
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
        return if (isExpired) {
            ExpiredAuctionGui(
                player = player,
                dependencies = dependencies,
                auctionComponent = auctionComponent
            )
        } else {
            AuctionGui(
                player = player,
                auctionComponent = auctionComponent,
                dependencies = dependencies,
            )
        }
    }
}
