package ru.astrainteractive.astramarket.gui.layout

import ru.astrainteractive.astramarket.api.layout.slotInventoryLayout

@Suppress("MagicNumber")
internal object DefaultAuctionInventoryLayoutFactory {

    private fun createCompactLayout() = slotInventoryLayout {
        repeat(5) {
            row(9, AuctionSlotKey.AUCTION_ITEM)
        }
        row(
            AuctionSlotKey.PREV_PAGE,
            AuctionSlotKey.EMPTY,
            AuctionSlotKey.DISPLAY_TYPE,
            AuctionSlotKey.FILTER_EXPIRED,
            AuctionSlotKey.BACK,
            AuctionSlotKey.SORT,
            AuctionSlotKey.EMPTY,
            AuctionSlotKey.EMPTY,
            AuctionSlotKey.NEXT_PAGE
        )
    }

    private fun createBorderedLayout() = slotInventoryLayout {
        row(9, AuctionSlotKey.BORDER)
        repeat(4) {
            row(
                AuctionSlotKey.BORDER,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.AUCTION_ITEM,
                AuctionSlotKey.BORDER
            )
        }
        row(
            AuctionSlotKey.BORDER,
            AuctionSlotKey.PREV_PAGE,
            AuctionSlotKey.DISPLAY_TYPE,
            AuctionSlotKey.FILTER_EXPIRED,
            AuctionSlotKey.BACK,
            AuctionSlotKey.SORT,
            AuctionSlotKey.EMPTY,
            AuctionSlotKey.NEXT_PAGE,
            AuctionSlotKey.BORDER
        )
    }

    fun create(isCompact: Boolean) = when {
        isCompact -> createCompactLayout()
        else -> createBorderedLayout()
    }
}
