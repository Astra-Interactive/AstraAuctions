package ru.astrainteractive.astramarket.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.StringFormat
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.async.DefaultBukkitDispatchers
import ru.astrainteractive.astralibs.encoding.encoder.BukkitObjectEncoder
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.menu.event.DefaultInventoryClickEvent
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.astramarket.core.LifecyclePlugin
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoderImpl
import ru.astrainteractive.astrashop.core.di.factory.ConfigKrateFactory
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate

interface BukkitCoreModule : CoreModule {

    val plugin: LifecyclePlugin
    val itemStackEncoder: ItemStackEncoder

    val inventoryClickEventListener: EventListener
    val kyoriComponentSerializer: Krate<KyoriComponentSerializer>

    class Default(override val plugin: LifecyclePlugin) : BukkitCoreModule {


        private val encoder: ObjectEncoder = BukkitObjectEncoder()

        override val itemStackEncoder: ItemStackEncoder = ItemStackEncoderImpl(encoder)

        override val inventoryClickEventListener = DefaultInventoryClickEvent()

        override val kyoriComponentSerializer = DefaultMutableKrate<KyoriComponentSerializer>(
            factory = { KyoriComponentSerializer.Legacy },
            loader = { null }
        )

        private fun createBStats() = Metrics(plugin, 15771)

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    createBStats()
                    inventoryClickEventListener.onEnable(plugin)
                },
                onDisable = {
                    inventoryClickEventListener.onEnable(plugin)
                }
            )
        }

        override val yamlStringFormat: StringFormat = YamlStringFormat()

        override val config: Krate<PluginConfig> = ConfigKrateFactory.create(
            fileNameWithoutExtension = "config",
            stringFormat = yamlStringFormat,
            dataFolder = plugin.dataFolder,
            factory = ::PluginConfig
        )

        override val translation: Krate<Translation> = ConfigKrateFactory.create(
            fileNameWithoutExtension = "translations",
            stringFormat = yamlStringFormat,
            dataFolder = plugin.dataFolder,
            factory = ::Translation
        )

        override val scope: CoroutineScope = CoroutineFeature.Default(Dispatchers.IO)

        override val dispatchers = DefaultBukkitDispatchers(plugin)

        override val economyProviderFactory: CurrencyEconomyProviderFactory =
            BukkitCurrencyEconomyProviderFactory(plugin)
    }
}
