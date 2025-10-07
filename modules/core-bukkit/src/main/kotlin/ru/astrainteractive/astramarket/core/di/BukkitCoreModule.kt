package ru.astrainteractive.astramarket.core.di

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.StringFormat
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.async.withTimings
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

    val plugin: LifecyclePlugin
    val itemStackEncoder: ItemStackEncoder
    val inventoryClickEventListener: EventListener
    val kyoriComponentSerializer: CachedKrate<KyoriComponentSerializer>

    class Default(override val plugin: LifecyclePlugin) : BukkitCoreModule {

        private val encoder: ObjectEncoder = BukkitObjectEncoder()

        override val itemStackEncoder: ItemStackEncoder = ItemStackEncoderImpl(encoder)

        override val inventoryClickEventListener = DefaultInventoryClickEvent()

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            factory = { KyoriComponentSerializer.Legacy },
            loader = { null }
        ).asCachedKrate()

        private fun createBStats() = Metrics(plugin, 15771)

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
                    file = plugin.dataFolder.resolve("config.yml"),
                    default = ::PluginConfig
                )
            }
        ).asCachedKrate()

        override val pluginTranslationKrate: CachedKrate<PluginTranslation> = DefaultMutableKrate(
            factory = ::PluginTranslation,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = plugin.dataFolder.resolve("translations.yml"),
                    default = ::PluginTranslation
                )
            }
        ).asCachedKrate()

        override val scope: CoroutineScope = CoroutineFeature.IO.withTimings()

        override val dispatchers = DefaultBukkitDispatchers(plugin)

        override val economyProviderFactory: CurrencyEconomyProviderFactory =
            BukkitCurrencyEconomyProviderFactory(plugin)

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    createBStats()
                    inventoryClickEventListener.onEnable(plugin)
                },
                onDisable = {
                    inventoryClickEventListener.onDisable()
                    scope.cancel()
                },
                onReload = {
                    kyoriComponentSerializer.getValue()
                    configKrate.getValue()
                    pluginTranslationKrate.getValue()
                }
            )
        }
    }
}
