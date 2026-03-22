package ru.astrainteractive.astramarket.core.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.coroutines.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.encoding.encoder.BukkitObjectEncoder
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoderImpl
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asCachedKrate
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature

interface BukkitCoreModule : CoreModule {

    override val lifecyclePlugin: LifecyclePlugin
    val itemStackEncoder: ItemStackEncoder
    val inventoryClickEventListener: EventListener

    class Default(override val lifecyclePlugin: LifecyclePlugin) : BukkitCoreModule {

        private val encoder: ObjectEncoder = BukkitObjectEncoder()

        override val itemStackEncoder: ItemStackEncoder = ItemStackEncoderImpl(encoder)

        override val inventoryClickEventListener = DefaultInventoryClickEvent()

        override val kyoriKrate = DefaultMutableKrate<KyoriComponentSerializer>(
            factory = { KyoriComponentSerializer.Legacy },
            loader = { null }
        ).asCachedKrate()

        @Suppress("MagicNumber")
        private fun createBStats() = Metrics(lifecyclePlugin, 15771)

        override val yamlStringFormat: StringFormat = YamlStringFormat(
            configuration = Yaml.default.configuration.copy(
                encodeDefaults = true,
                strictMode = false,
                polymorphismStyle = PolymorphismStyle.Property
            ),
        )

        override val configKrate: CachedKrate<PluginConfig> = DefaultMutableKrate(
            factory = ::PluginConfig,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = lifecyclePlugin.dataFolder.resolve("config.yml"),
                    default = ::PluginConfig
                )
            }
        ).asCachedKrate()

        override val pluginTranslationKrate: CachedKrate<PluginTranslation> = DefaultMutableKrate(
            factory = ::PluginTranslation,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = lifecyclePlugin.dataFolder.resolve("translations.yml"),
                    default = ::PluginTranslation
                )
            }
        ).asCachedKrate()
        override val dispatchers = DefaultBukkitDispatchers(lifecyclePlugin)
        override val ioScope: CoroutineScope = CoroutineFeature.IO.withTimings()
        override val mainScope = CoroutineFeature
            .Default(dispatchers.BukkitMain)
            .withTimings()

        override val economyProviderFactory: CurrencyEconomyProviderFactory =
            BukkitCurrencyEconomyProviderFactory(lifecyclePlugin)

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    createBStats()
                    inventoryClickEventListener.onEnable(lifecyclePlugin)
                },
                onDisable = {
                    inventoryClickEventListener.onDisable()
                    ioScope.cancel()
                },
                onReload = {
                    kyoriKrate.getValue()
                    configKrate.getValue()
                    pluginTranslationKrate.getValue()
                }
            )
        }
    }
}
