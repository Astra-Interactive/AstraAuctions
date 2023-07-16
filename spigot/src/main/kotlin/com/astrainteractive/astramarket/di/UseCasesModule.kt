package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.gui.domain.usecases.AuctionBuyUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.CreateAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.ExpireAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.RemoveAuctionUseCase
import ru.astrainteractive.klibs.kdi.Module

interface UseCasesModule : Module {
    val auctionBuyUseCase: AuctionBuyUseCase
    val createAuctionUseCase: CreateAuctionUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase
}
