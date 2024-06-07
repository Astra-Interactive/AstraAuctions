package ru.astrainteractive.astramarket.gui.invmap

import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey

object DefaultAuctionInventoryMap : AuctionInventoryMap {
    @Suppress("MaxLineLength")
    override val map: Array<Array<AuctionSlotKey>> = arrayOf(
        arrayOf(AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI),
        arrayOf(AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI),
        arrayOf(AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI),
        arrayOf(AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI),
        arrayOf(AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI, AuctionSlotKey.AI),
        arrayOf(AuctionSlotKey.PR, AuctionSlotKey.EM, AuctionSlotKey.EM, AuctionSlotKey.AU, AuctionSlotKey.BA, AuctionSlotKey.FI, AuctionSlotKey.EM, AuctionSlotKey.EM, AuctionSlotKey.NE),
    )
}
