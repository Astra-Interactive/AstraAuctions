package ru.astrainteractive.astramarket.presentation.invmap

import ru.astrainteractive.astramarket.presentation.invmap.AuctionInventoryMap.AuctionSlotKey

interface AuctionInventoryMap : InventoryMap<AuctionSlotKey> {
    enum class AuctionSlotKey {
        // Border
        BO,

        // Prev
        PR,

        // Next
        NE,

        // Auction/Expired
        AU,

        // Back
        BA,

        // Filter
        FI,

        // Auction item
        AI,

        // Empty
        EM
    }
}
