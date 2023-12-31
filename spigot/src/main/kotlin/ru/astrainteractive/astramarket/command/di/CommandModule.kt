package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.command.common.CommonCommandFactory
import ru.astrainteractive.astramarket.command.common.di.CommonCommandDependencies
import ru.astrainteractive.astramarket.di.RootModule

interface CommandModule {
    val lifecycle: Lifecycle

    class Default(rootModule: RootModule) : CommandModule {
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    CommonCommandFactory(
                        dependencies = CommonCommandDependencies.Default(rootModule)
                    ).create()
                    AuctionCommandFactory(
                        dependencies = AuctionCommandDependencies.Default(rootModule)
                    ).create()
                }
            )
        }
    }
}
