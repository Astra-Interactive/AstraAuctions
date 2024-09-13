package ru.astrainteractive.astramarket.di

import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.impl.ExposedMarketApi
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapperImpl
import ru.astrainteractive.astramarket.di.factory.DatabaseFactory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface ApiMarketModule {
    val lifecycle: Lifecycle

    val database: Database
    val marketApi: MarketApi

    class Default(
        dbConnection: DBConnection,
        dbSyntax: DBSyntax,
        dispatchers: KotlinDispatchers
    ) : ApiMarketModule {
        override val database: Database by lazy {
            DatabaseFactory(
                dbConnection = dbConnection,
                dbSyntax = dbSyntax
            ).create()
        }
        private val auctionMapper: AuctionMapper by Provider {
            AuctionMapperImpl()
        }

        override val marketApi: MarketApi by Provider {
            ExposedMarketApi(
                database = database,
                auctionMapper = auctionMapper,
                dispatchers = dispatchers
            )
        }
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    runBlocking { database.openConnection() }
                },
                onDisable = {
                    runBlocking { database.closeConnection() }
                }
            )
        }
    }
}
