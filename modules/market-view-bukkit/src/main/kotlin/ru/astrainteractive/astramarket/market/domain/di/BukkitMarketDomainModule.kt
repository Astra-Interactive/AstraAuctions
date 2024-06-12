package ru.astrainteractive.astramarket.market.domain.di

import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.usecase.BukkitSortAuctionsUseCase
import ru.astrainteractive.astramarket.market.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class BukkitMarketDomainModule(itemStackEncoder: ItemStackEncoder) : PlatformMarketDomainModule {
    override val sortAuctionsUseCase: SortAuctionsUseCase by Provider {
        BukkitSortAuctionsUseCase(
            itemStackEncoder = itemStackEncoder
        )
    }
}
