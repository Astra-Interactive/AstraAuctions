package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandRegistry
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.command.common.CommonCommandRegistry
import ru.astrainteractive.astramarket.command.common.di.CommonCommandDependencies
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketModule

interface CommandModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        routerModule: RouterModule,
        marketModule: MarketModule
    ) : CommandModule {
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    CommonCommandRegistry(
                        dependencies = CommonCommandDependencies.Default(
                            coreModule = coreModule,
                            bukkitCoreModule = bukkitCoreModule
                        )
                    ).register()
                    AuctionCommandRegistry(
                        dependencies = AuctionCommandDependencies.Default(
                            coreModule = coreModule,
                            bukkitCoreModule = bukkitCoreModule,
                            marketModule = marketModule,
                            routerModule = routerModule
                        )
                    ).register()
                }
            )
        }
    }
}
