package ru.astrainteractive.astramarket.command.common.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.klibs.kdi.getValue

interface CommonCommandDependencies {
    val plugin: JavaPlugin
    val translation: Translation
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(rootModule: RootModule) : CommonCommandDependencies {
        override val plugin: JavaPlugin by rootModule.bukkitCoreModule.plugin
        override val translation: Translation by rootModule.coreModule.translation
        override val kyoriComponentSerializer by rootModule.bukkitCoreModule.kyoriComponentSerializer
    }
}
