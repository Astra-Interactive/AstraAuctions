package ru.astrainteractive.astramarket.players.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.players.component.DefaultPlayersMarketComponent
import ru.astrainteractive.astramarket.players.component.PlayersMarketComponent

interface PlayersMarketViewModule {
    fun createPlayersMarketComponent(isExpired: Boolean): PlayersMarketComponent

    class Default(
        private val coreModule: CoreModule,
        private val marketRepository: MarketRepository
    ) : PlayersMarketViewModule {
        override fun createPlayersMarketComponent(isExpired: Boolean): PlayersMarketComponent {
            return DefaultPlayersMarketComponent(
                marketRepository = marketRepository,
                dispatchers = coreModule.dispatchers,
                isExpired = isExpired
            )
        }
    }
}
