package ru.astrainteractive.astramarket.domain.di

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.domain.usecase.BukkitSortAuctionsUseCase
import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitSharedDomainModule(encoder: ObjectEncoder) : PlatformSharedDomainModule {
    override val sortAuctionsUseCase: SortAuctionsUseCase by Provider {
        BukkitSortAuctionsUseCase(
            encoder = encoder
        )
    }
}
