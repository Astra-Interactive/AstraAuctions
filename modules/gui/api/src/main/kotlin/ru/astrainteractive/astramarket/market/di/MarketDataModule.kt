package ru.astrainteractive.astramarket.market.di

import ru.astrainteractive.astramarket.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge

interface MarketDataModule {
    val auctionBridge: AuctionsBridge
    val playerInteractionBridge: PlayerInteractionBridge
}
