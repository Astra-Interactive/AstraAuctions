package ru.astrainteractive.astramarket.di.impl

import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProviderFactory
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.di.BukkitSharedDataModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.di.BukkitCoreModule
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
    override val coreModule: CoreModule by lazy {
        CoreModule.Default(
            dataFolder = bukkitCoreModule.plugin.value.dataFolder,
            dispatchers = DefaultBukkitDispatchers(bukkitCoreModule.plugin.value),
            economyProvider = EconomyProviderFactory(bukkitCoreModule.plugin.value).create()
        )
    }
    override val apiMarketModule: ApiMarketModule by Provider {
        val config by coreModule.config
        val (dbConnection, dbSyntax) = config.connection.toDBConnection()
        ApiMarketModule.Default(
            dbConnection = dbConnection,
            dbSyntax = dbSyntax,
            dispatchers = coreModule.dispatchers
        )
    }

    override val sharedDomainModule: SharedDomainModule by Provider {
        SharedDomainModule.Default(
            coreModule = coreModule,
            apiMarketModule = apiMarketModule,
            sharedDataModuleFactory = {
                BukkitSharedDataModule(
                    encoder = bukkitCoreModule.encoder.value,
                    stringSerializer = bukkitCoreModule.kyoriComponentSerializer.value
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
    override val commandModule: CommandModule by lazy {
        CommandModule.Default(this)
    }
}
