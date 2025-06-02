package ru.astrainteractive.astramarket.command.common.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.LifecyclePlugin
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.klibs.kstorage.util.getValue

internal interface CommonCommandDependencies {
    val plugin: LifecyclePlugin
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        bukkitCoreModule: BukkitCoreModule,
        coreModule: CoreModule
    ) : CommonCommandDependencies {
        override val plugin: LifecyclePlugin = bukkitCoreModule.plugin
        override val translation: Translation by coreModule.translationKrate
        override val kyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
    }
}
