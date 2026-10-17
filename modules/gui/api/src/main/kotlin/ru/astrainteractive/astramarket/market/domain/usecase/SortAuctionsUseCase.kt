package ru.astrainteractive.astramarket.market.domain.usecase

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.model.AuctionSort

interface SortAuctionsUseCase {
    class Input(val sortType: AuctionSort, val list: List<MarketSlot>)
    class Output(val list: List<MarketSlot>)
    fun invoke(input: SortAuctionsUseCase.Input): SortAuctionsUseCase.Output
}
