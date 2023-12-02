package ru.astrainteractive.astramarket.presentation.di

import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge
import ru.astrainteractive.astramarket.domain.usecase.AuctionBuyUseCase
import ru.astrainteractive.astramarket.domain.usecase.ExpireAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.RemoveAuctionUseCase
import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.plugin.AuctionConfig

interface AuctionComponentDependencies {
    val config: AuctionConfig
    val dispatchers: BukkitDispatchers
    val auctionsAPI: AuctionsAPI
    val auctionBuyUseCase: AuctionBuyUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase
    val playerInteractionBridge: PlayerInteractionBridge
    val sortAuctionsUseCase: SortAuctionsUseCase
}
