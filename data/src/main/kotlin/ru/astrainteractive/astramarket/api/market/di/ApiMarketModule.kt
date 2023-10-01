package ru.astrainteractive.astramarket.api.market.di

import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.impl.AuctionsAPIImpl
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapper
import ru.astrainteractive.astramarket.api.market.mapping.AuctionMapperImpl
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface ApiMarketModule {
    val auctionApi: AuctionsAPI
    val auctionMapper: AuctionMapper

    class Default(
        database: Database,
        dispatchers: KotlinDispatchers
    ) : ApiMarketModule {
        override val auctionMapper: AuctionMapper by Provider {
            AuctionMapperImpl()
        }

        override val auctionApi: AuctionsAPI by Provider {
            AuctionsAPIImpl(
                database = database,
                auctionMapper = auctionMapper,
                dispatchers = dispatchers
            )
        }
    }
}
