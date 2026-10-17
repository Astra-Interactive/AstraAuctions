package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.market.api.AuctionsSorter

interface PlatformMarketDomainModule {
    val auctionsSorter: AuctionsSorter
}
