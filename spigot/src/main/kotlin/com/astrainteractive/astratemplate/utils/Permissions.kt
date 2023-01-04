package com.astrainteractive.astratemplate.utils

/**
 * Permission class.
 *
 * All permission should be stored in companion object
 */
object Permissions {
    val reload: String
        get() = "astra_market.reload"
    val sell: String
        get() = "astra_market.sell"
    val sellMax: String
        get() = "astra_market.sell_max"
    val expire: String
        get() = "astra_market.expire"
}