package ru.astrainteractive.astramarket.market.fake

import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astramarket.core.economy.api.CurrencyEconomyProviderResolver

class FakeCurrencyEconomyProviderResolver(
    private val defaultEconomy: EconomyFacade?
) : CurrencyEconomyProviderResolver {
    override fun findByCurrencyId(currencyId: String): EconomyFacade? = defaultEconomy

    override fun findDefault(): EconomyFacade? = defaultEconomy
}
