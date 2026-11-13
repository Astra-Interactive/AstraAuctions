@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.gui.bukkit.market.util

import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.market.model.AuctionSort
import kotlin.test.Test
import kotlin.test.assertSame

class AuctionSortTranslationTest {
    private val translation = PluginTranslation()
    private val sort = translation.menu.sort

    @Test
    fun GIVEN_each_auction_sort_WHEN_translated_THEN_returns_its_label() {
        assertSame(sort.byDate, selectAuctionSortTranslation(AuctionSort.Date(isAsc = true), translation))
        assertSame(sort.byMaterial, selectAuctionSortTranslation(AuctionSort.Material(isAsc = true), translation))
        assertSame(sort.byName, selectAuctionSortTranslation(AuctionSort.Name(isAsc = false), translation))
        assertSame(sort.byPrice, selectAuctionSortTranslation(AuctionSort.Price(isAsc = false), translation))
        assertSame(sort.byPlayer, selectAuctionSortTranslation(AuctionSort.Player(isAsc = true), translation))
    }
}
