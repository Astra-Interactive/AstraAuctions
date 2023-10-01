package ru.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.util.Permission

sealed class PluginPermission(override val value: String) : Permission {
    data object Reload : PluginPermission("astra_market.reload")
    data object Sell : PluginPermission("astra_market.sell")
    data object SellMax : PluginPermission("astra_market.sell_max")
    data object Expire : PluginPermission("astra_market.expire")
    data object Amarket : PluginPermission("astra_market.command")
}
