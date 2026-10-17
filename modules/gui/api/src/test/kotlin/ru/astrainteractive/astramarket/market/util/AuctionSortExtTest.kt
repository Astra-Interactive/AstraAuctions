@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.market.util

import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.market.model.AuctionSort
import kotlin.test.Test
import kotlin.test.assertSame

class AuctionSortExtTest {
    private val translation = PluginTranslation()
    private val sort = translation.menu.sort

    @Test
    fun GIVEN_each_auction_sort_WHEN_translated_THEN_returns_its_label() {
        assertSame(sort.byDate, AuctionSort.Date(isAsc = true).toTranslation(translation))
        assertSame(sort.byMaterial, AuctionSort.Material(isAsc = true).toTranslation(translation))
        assertSame(sort.byName, AuctionSort.Name(isAsc = false).toTranslation(translation))
        assertSame(sort.byPrice, AuctionSort.Price(isAsc = false).toTranslation(translation))
        assertSame(sort.byPlayer, AuctionSort.Player(isAsc = true).toTranslation(translation))
    }
}
