package ru.astrainteractive.astramarket.core.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.filemanager.impl.JVMResourceFileManager
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface CoreModule {
    val lifecycle: Lifecycle
    val config: Dependency<PluginConfig>
    val translation: Dependency<Translation>
    val scope: Dependency<AsyncComponent>
    val dispatchers: KotlinDispatchers
    val economyProvider: EconomyProvider

    class Default(
        dataFolder: File,
        override val dispatchers: KotlinDispatchers,
        override val economyProvider: EconomyProvider
    ) : CoreModule {

        override val translation: Reloadable<Translation> = Reloadable {
            val fileManager = JVMResourceFileManager("translations.yml", dataFolder, this::class.java)
            val serializer = YamlSerializer()
            serializer.parse<Translation>(fileManager.configFile)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { Translation() }
                .also { serializer.writeIntoFile(it, fileManager.configFile) }
        }

        override val config: Reloadable<PluginConfig> = Reloadable {
            val fileManager = JVMResourceFileManager("config.yml", dataFolder, this::class.java)
            val serializer = YamlSerializer()
            serializer.parse<PluginConfig>(fileManager.configFile)
                .onFailure(Throwable::printStackTrace)
                .getOrElse { PluginConfig() }
                .also { serializer.writeIntoFile(it, fileManager.configFile) }
        }
        override val scope: Dependency<AsyncComponent> = Single {
            AsyncComponent.Default()
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onReload = {
                    config.reload()
                    translation.reload()
                },
                onDisable = {
                    scope.value.close()
                }
            )
        }
    }
}
