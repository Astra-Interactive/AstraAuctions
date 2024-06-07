package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

interface SortAuctionsUseCase : UseCase.Blocking<SortAuctionsUseCase.Input, SortAuctionsUseCase.Output> {
    class Input(val sortType: AuctionSort, val list: List<MarketSlot>)
    class Output(val list: List<MarketSlot>)
}
