package ru.astrainteractive.astramarket.di

import ru.astrainteractive.astramarket.data.di.SharedDataModule
import ru.astrainteractive.astramarket.domain.di.BukkitSharedDomainModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.presentation.di.AuctionGuiModule
import ru.astrainteractive.klibs.kdi.Module

interface RootModule : Module {
    val bukkitCoreModule: BukkitCoreModule
    val dataModule: DataModule
    val auctionGuiModule: AuctionGuiModule
    val bukkitSharedDomainModule: BukkitSharedDomainModule
    val sharedDataModule: SharedDataModule
    val sharedDomainModule: SharedDomainModule
}
