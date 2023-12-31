package ru.astrainteractive.astramarket.data.di

import ru.astrainteractive.astramarket.data.bridge.AuctionsBridge
import ru.astrainteractive.astramarket.data.bridge.PlayerInteractionBridge

interface SharedDataModule {
    val auctionBridge: AuctionsBridge
    val playerInteractionBridge: PlayerInteractionBridge
}
