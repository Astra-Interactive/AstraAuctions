package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort

interface SortAuctionsUseCase {
    fun invoke(input: SortAuctionsUseCase.Input): SortAuctionsUseCase.Output
    class Input(val sortType: AuctionSort, val list: List<MarketSlot>)
    class Output(val list: List<MarketSlot>)
}
