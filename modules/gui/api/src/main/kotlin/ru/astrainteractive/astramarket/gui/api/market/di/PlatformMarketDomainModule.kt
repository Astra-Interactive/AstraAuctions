package ru.astrainteractive.astramarket.gui.api.market.di

import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsSorter

interface PlatformMarketDomainModule {
    val auctionsSorter: AuctionsSorter
}
