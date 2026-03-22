package ru.astrainteractive.astramarket.players.di

import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMapping
import ru.astrainteractive.astramarket.players.mapping.PlayerSortTranslationMappingImpl
import ru.astrainteractive.astramarket.players.presentation.DefaultPlayersMarketComponent
import ru.astrainteractive.astramarket.players.presentation.PlayersMarketComponent

interface PlayersMarketViewModule {
    val playerSortTranslationMapping: PlayerSortTranslationMapping

    fun createPlayersMarketComponent(isExpired: Boolean): PlayersMarketComponent

    class Default(
        private val coreModule: CoreModule,
        private val apiMarketModule: ApiMarketModule
    ) : PlayersMarketViewModule {
        override val playerSortTranslationMapping: PlayerSortTranslationMapping by lazy {
            PlayerSortTranslationMappingImpl(
                pluginTranslationKrate = coreModule.pluginTranslationKrate
            )
        }

        override fun createPlayersMarketComponent(isExpired: Boolean): PlayersMarketComponent {
            return DefaultPlayersMarketComponent(
                marketApi = apiMarketModule.marketApi,
                dispatchers = coreModule.dispatchers,
                isExpired = isExpired
            )
        }
    }
}
