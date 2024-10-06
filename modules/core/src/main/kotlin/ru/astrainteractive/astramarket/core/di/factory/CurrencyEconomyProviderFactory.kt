package ru.astrainteractive.astramarket.core.di.factory

import ru.astrainteractive.astralibs.economy.EconomyFacade

interface CurrencyEconomyProviderFactory {
    fun findByCurrencyId(currencyId: String): EconomyFacade?
    fun findDefault(): EconomyFacade?
}
