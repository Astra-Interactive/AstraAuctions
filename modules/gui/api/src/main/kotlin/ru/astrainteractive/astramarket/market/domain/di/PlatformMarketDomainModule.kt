package ru.astrainteractive.astramarket.market.domain.di

import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase

interface PlatformMarketDomainModule {
    val sortAuctionsUseCase: SortAuctionsUseCase
}
