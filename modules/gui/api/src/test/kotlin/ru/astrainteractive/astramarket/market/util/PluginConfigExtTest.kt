@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.market.util

import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PluginConfigExtTest {
    private val auction = PluginConfig.Auction(minPrice = 10, maxPrice = 100, maxAuctionPerPlayer = 2)

    @Test
    fun GIVEN_price_on_the_bounds_WHEN_checked_THEN_price_is_allowed() {
        assertTrue(auction.isPriceAllowed(10f))
        assertTrue(auction.isPriceAllowed(100f))
    }

    @Test
    fun GIVEN_price_out_of_bounds_WHEN_checked_THEN_price_is_not_allowed() {
        assertFalse(auction.isPriceAllowed(9.99f))
        assertFalse(auction.isPriceAllowed(100.01f))
    }

    @Test
    fun GIVEN_no_permission_limit_WHEN_listing_THEN_config_limit_applies() {
        assertTrue(auction.canListMore(listedAmount = 1, permissionLimit = null))
        assertFalse(auction.canListMore(listedAmount = 2, permissionLimit = null))
    }

    @Test
    fun GIVEN_permission_limit_WHEN_listing_THEN_permission_limit_wins() {
        assertTrue(auction.canListMore(listedAmount = 4, permissionLimit = 5))
        assertFalse(auction.canListMore(listedAmount = 1, permissionLimit = 1))
    }
}
