package ru.astrainteractive.astramarket.di

import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.impl.AuctionsApiImpl
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapperImpl
import ru.astrainteractive.astramarket.di.factory.DatabaseFactory
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface DataModule {
    val database: Database
    val auctionApi: AuctionsAPI

    class Default(
        dbConnection: DBConnection,
        dbSyntax: DBSyntax,
        dispatchers: KotlinDispatchers
    ) : DataModule {
        override val database: Database by lazy {
            DatabaseFactory(
                dbConnection = dbConnection,
                dbSyntax = dbSyntax
            ).create()
        }
        private val auctionMapper: AuctionMapper by Provider {
            AuctionMapperImpl()
        }

        override val auctionApi: AuctionsAPI by Provider {
            AuctionsApiImpl(
                database = database,
                auctionMapper = auctionMapper,
                dispatchers = dispatchers
            )
        }
    }
}
