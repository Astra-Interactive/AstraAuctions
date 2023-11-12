package ru.astrainteractive.astramarket.domain.di

import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase

interface PlatformSharedDomainModule {
    val sortAuctionsUseCase: SortAuctionsUseCase
}
