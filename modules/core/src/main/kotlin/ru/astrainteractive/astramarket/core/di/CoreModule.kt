package ru.astrainteractive.astramarket.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CoreModule {
    val lifecycle: Lifecycle

    val config: Krate<PluginConfig>
    val translation: Krate<Translation>
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val yamlStringFormat: StringFormat
    val economyProviderFactory: CurrencyEconomyProviderFactory
}
