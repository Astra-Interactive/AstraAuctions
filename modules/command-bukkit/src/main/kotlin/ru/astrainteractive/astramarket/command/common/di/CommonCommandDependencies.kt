package ru.astrainteractive.astramarket.command.common.di

import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.AstraMarketPlugin
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.klibs.kdi.getValue

internal interface CommonCommandDependencies {
    val plugin: AstraMarketPlugin
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        bukkitCoreModule: BukkitCoreModule,
        coreModule: CoreModule
    ) : CommonCommandDependencies {
        override val plugin: AstraMarketPlugin by bukkitCoreModule.plugin
        override val translation: Translation by coreModule.translation
        override val kyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
    }
}
