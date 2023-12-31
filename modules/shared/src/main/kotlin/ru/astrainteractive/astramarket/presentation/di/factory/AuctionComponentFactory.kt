package ru.astrainteractive.astramarket.presentation.di.factory

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.di.SharedDataModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.DefaultAuctionComponent
import ru.astrainteractive.astramarket.presentation.di.AuctionComponentDependencies
import ru.astrainteractive.klibs.kdi.Factory
import java.util.UUID

class AuctionComponentFactory(
    private val playerUUID: UUID,
    private val isExpired: Boolean,
    private val coreModule: CoreModule,
    private val apiMarketModule: ApiMarketModule,
    private val sharedDomainModule: SharedDomainModule,
    private val sharedDataModule: SharedDataModule
) : Factory<AuctionComponent> {
    override fun create(): AuctionComponent {
        val dependencies = AuctionComponentDependencies.Default(
            coreModule = coreModule,
            apiMarketModule = apiMarketModule,
            sharedDomainModule = sharedDomainModule,
            sharedDataModule = sharedDataModule
        )
        return DefaultAuctionComponent(
            playerUUID = playerUUID,
            isExpired = isExpired,
            dependencies = dependencies
        )
    }
}
