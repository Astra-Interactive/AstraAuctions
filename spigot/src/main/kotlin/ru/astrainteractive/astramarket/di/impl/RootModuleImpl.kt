package ru.astrainteractive.astramarket.di.impl

import ru.astrainteractive.astramarket.data.di.BukkitSharedDataModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
import ru.astrainteractive.astramarket.di.DataModule
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.di.util.ConnectionExt.toDBConnection
import ru.astrainteractive.astramarket.domain.di.BukkitSharedDomainModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.presentation.di.AuctionGuiModule
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class RootModuleImpl : RootModule {

    override val bukkitCoreModule: BukkitCoreModule by lazy {
        BukkitCoreModuleImpl()
    }
    override val dataModule: DataModule by Provider {
        val config by bukkitCoreModule.configuration
        val (dbConnection, dbSyntax) = config.connection.toDBConnection()
        DataModule.Default(
            dbConnection = dbConnection,
            dbSyntax = dbSyntax,
            dispatchers = bukkitCoreModule.dispatchers.value
        )
    }

    override val sharedDomainModule: SharedDomainModule by Provider {
        SharedDomainModule.Default(
            translation = bukkitCoreModule.translation.value,
            configuration = bukkitCoreModule.configuration.value,
            economyProvider = bukkitCoreModule.economyProvider.value,
            sharedDataModuleFactory = {
                BukkitSharedDataModule(
                    dataModule = dataModule,
                    encoder = bukkitCoreModule.encoder.value,
                    stringSerializer = bukkitCoreModule.stringSerializer.value
                )
            },
            platformSharedDomainModuleFactory = {
                BukkitSharedDomainModule(
                    encoder = bukkitCoreModule.encoder.value,
                )
            }

        )
    }
    override val auctionGuiModule: AuctionGuiModule by Provider {
        AuctionGuiModule.Default(this)
    }
}
