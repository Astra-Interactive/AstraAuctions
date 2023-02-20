package com.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.utils.Permission

sealed class PluginPermission(override val value: String) : Permission {
    object Reload : PluginPermission("astra_market.reload")
    object Sell : PluginPermission("astra_market.sell")
    object SellMax : PluginPermission("astra_market.sell_max")
    object Expire : PluginPermission("astra_market.expire")
    object Amarket : PluginPermission("astra_market.command")

}