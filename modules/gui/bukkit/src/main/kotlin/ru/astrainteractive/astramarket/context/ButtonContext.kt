package ru.astrainteractive.astramarket.context

import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.Locale

/** What the buttons of one menu need, including the language of the player who opened the menu. */
internal interface ButtonContext : Logger {
    val config: PluginConfig
    val pluginTranslation: PluginTranslation
    val objectEncoder: ObjectEncoder
    val locale: Locale

    class Default(
        coreModule: CoreModule,
        override val objectEncoder: ObjectEncoder,
        override val locale: Locale
    ) : ButtonContext,
        Logger by JUtiltLogger("AstraMarket-ButtonContext").withoutParentHandlers() {
        override val config: PluginConfig by coreModule.configKrate
        override val pluginTranslation: PluginTranslation by coreModule.pluginTranslationKrate
    }
}
