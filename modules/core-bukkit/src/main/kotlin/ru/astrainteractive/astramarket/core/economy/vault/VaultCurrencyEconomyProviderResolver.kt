package ru.astrainteractive.astramarket.core.economy.vault

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.economy.EssentialsEconomyFacade
import ru.astrainteractive.astralibs.economy.VaultEconomyFacade
import ru.astrainteractive.astramarket.core.economy.api.CurrencyEconomyProviderResolver
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class VaultCurrencyEconomyProviderResolver(
    private val plugin: JavaPlugin,
) : CurrencyEconomyProviderResolver,
    Logger by JUtiltLogger("AstraMarket-CurrencyEconomyProviderResolver").withoutParentHandlers() {
    override fun findByCurrencyId(currencyId: String): EconomyFacade? {
        val registrations = Bukkit.getServer().servicesManager.getRegistrations(Economy::class.java)
        info { "#findEconomyProviderByCurrency registrations: ${registrations.size}" }
        val specificEconomyProvider = registrations
            .firstOrNull { registration -> registration.provider.currencyNameSingular() == currencyId }
            ?.provider
            ?.let(::VaultEconomyFacade)
        if (specificEconomyProvider == null) {
            error { "#economyProvider could not find economy with currency: $currencyId" }
        }
        return specificEconomyProvider
    }

    override fun findDefault(): EconomyFacade? {
        return kotlin.runCatching {
            VaultEconomyFacade(plugin)
        }.getOrNull() ?: kotlin.runCatching {
            if (!Bukkit.getServer().pluginManager.isPluginEnabled("Essentials")) {
                error("Essentials not enabled")
            } else {
                EssentialsEconomyFacade
            }
        }.getOrNull()
    }
}
