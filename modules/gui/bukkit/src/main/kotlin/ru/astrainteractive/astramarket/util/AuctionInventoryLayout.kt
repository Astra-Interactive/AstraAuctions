package ru.astrainteractive.astramarket.util

import ru.astrainteractive.astralibs.menu.layout.InventoryLayout
import ru.astrainteractive.astralibs.menu.layout.slotInventoryLayout
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astramarket.model.AuctionSlotKey

@Suppress("MagicNumber")
private fun compactAuctionLayout(): InventoryLayout<AuctionSlotKey, InventorySlot> {
    return slotInventoryLayout {
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
}

@Suppress("MagicNumber")
private fun borderedAuctionLayout(): InventoryLayout<AuctionSlotKey, InventorySlot> {
    return slotInventoryLayout {
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
}

internal fun auctionInventoryLayout(isCompact: Boolean): InventoryLayout<AuctionSlotKey, InventorySlot> {
    return when {
        isCompact -> compactAuctionLayout()
        else -> borderedAuctionLayout()
    }
}
