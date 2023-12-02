package ru.astrainteractive.astramarket.data.di

import ru.astrainteractive.astramarket.data.AuctionsBridge
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge

interface SharedDataModule {
    val auctionBridge: AuctionsBridge
    val playerInteractionBridge: PlayerInteractionBridge
}
