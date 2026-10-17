package ru.astrainteractive.astramarket.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.StringFormat
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.command.CommandExceptionHandler
import ru.astrainteractive.astramarket.core.economy.api.CurrencyEconomyProviderFactory
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CoreModule {
    val lifecycle: Lifecycle
    val lifecyclePlugin: Lifecycle

    val configKrate: CachedKrate<PluginConfig>
    val pluginTranslationKrate: CachedKrate<PluginTranslation>
    val ioScope: CoroutineScope
    val mainScope: CoroutineScope
    val unconfinedScope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val yamlStringFormat: StringFormat
    val economyProviderFactory: CurrencyEconomyProviderFactory
    val multiplatformCommand: MultiplatformCommand
    val commandExceptionHandler: CommandExceptionHandler
}
