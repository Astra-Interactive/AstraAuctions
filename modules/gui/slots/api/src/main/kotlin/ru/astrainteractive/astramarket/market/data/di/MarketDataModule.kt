package ru.astrainteractive.astramarket.market.data.di

import ru.astrainteractive.astramarket.market.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.market.data.bridge.PlayerInteractionBridge

interface MarketDataModule {
    val auctionBridge: AuctionsBridge
    val playerInteractionBridge: PlayerInteractionBridge
}
