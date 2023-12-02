package ru.astrainteractive.astramarket.domain.usecase

import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.model.AuctionSort
import ru.astrainteractive.klibs.mikro.core.domain.UseCase

interface SortAuctionsUseCase : UseCase.Blocking<SortAuctionsUseCase.Input, SortAuctionsUseCase.Output> {
    class Input(val sortType: AuctionSort, val list: List<AuctionDTO>)
    class Output(val list: List<AuctionDTO>)
}
