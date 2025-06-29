package ru.astrainteractive.astramarket.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CoreModule {
    val lifecycle: Lifecycle

    val configKrate: CachedKrate<PluginConfig>
    val pluginTranslationKrate: CachedKrate<PluginTranslation>
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val yamlStringFormat: StringFormat
    val economyProviderFactory: CurrencyEconomyProviderFactory
}
