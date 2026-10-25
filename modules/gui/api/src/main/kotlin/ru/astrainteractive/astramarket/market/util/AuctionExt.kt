package ru.astrainteractive.astramarket.market.util

import ru.astrainteractive.astramarket.core.settings.model.PluginConfig

fun PluginConfig.Auction.isPriceAllowed(price: Float): Boolean {
    return (price <= maxPrice).and(price >= minPrice)
}

fun PluginConfig.Auction.canListMore(listedAmount: Int, permissionLimit: Int?): Boolean {
    val limit = permissionLimit ?: maxAuctionPerPlayer
    return listedAmount < limit
}
