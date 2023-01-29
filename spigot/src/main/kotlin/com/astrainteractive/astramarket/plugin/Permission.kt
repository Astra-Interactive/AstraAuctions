package com.astrainteractive.astramarket.plugin

import ru.astrainteractive.astralibs.utils.IPermission

sealed class Permission(override val value: String) : IPermission {
    object Reload : Permission("astra_market.reload")
    object Sell : Permission("astra_market.sell")
    object SellMax : Permission("astra_market.sell_max")
    object Expire : Permission("astra_market.expire")

}