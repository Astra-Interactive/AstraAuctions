package ru.astrainteractive.astramarket.market.fake

import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astramarket.core.economy.api.CurrencyEconomyProviderFactory

class FakeCurrencyEconomyProviderFactory(
    private val defaultEconomy: EconomyFacade?
) : CurrencyEconomyProviderFactory {
    override fun findByCurrencyId(currencyId: String): EconomyFacade? = defaultEconomy

    override fun findDefault(): EconomyFacade? = defaultEconomy
}
