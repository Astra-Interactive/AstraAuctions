package ru.astrainteractive.astramarket.gui.invmap

import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey

internal interface AuctionInventoryMap : InventoryMap<AuctionSlotKey> {
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

        // Group by player or all slots
        GR,

        // Empty
        EM
    }
}
