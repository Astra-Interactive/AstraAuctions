package ru.astrainteractive.astramarket.di

import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.presentation.di.AuctionGuiModule

interface RootModule {
    val coreModule: CoreModule
    val bukkitCoreModule: BukkitCoreModule
    val apiMarketModule: ApiMarketModule
    val auctionGuiModule: AuctionGuiModule
    val sharedDomainModule: SharedDomainModule
    val commandModule: CommandModule
}
