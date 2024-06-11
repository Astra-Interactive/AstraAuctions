package ru.astrainteractive.astramarket.market.domain.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.market.domain.usecase.BukkitSortAuctionsUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitMarketDomainModule(encoder: ObjectEncoder) : PlatformMarketDomainModule {
    override val sortAuctionsUseCase: SortAuctionsUseCase by Provider {
        BukkitSortAuctionsUseCase(
            encoder = encoder
        )
    }
}
