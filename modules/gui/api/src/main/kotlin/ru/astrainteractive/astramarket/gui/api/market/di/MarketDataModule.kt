package ru.astrainteractive.astramarket.gui.api.market.di

import ru.astrainteractive.astramarket.gui.api.market.api.AuctionsBridge
import ru.astrainteractive.astramarket.gui.api.market.api.PlayerInteractionBridge

interface MarketDataModule {
    val auctionBridge: AuctionsBridge
    val playerInteractionBridge: PlayerInteractionBridge
}
