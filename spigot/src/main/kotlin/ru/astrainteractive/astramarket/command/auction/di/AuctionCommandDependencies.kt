package ru.astrainteractive.astramarket.command.auction.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface AuctionCommandDependencies {
    val plugin: JavaPlugin
    val kyoriComponentSerializer: KyoriComponentSerializer
    val translation: Translation
    val router: GuiRouter
    val encoder: ObjectEncoder
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val createAuctionUseCase: CreateAuctionUseCase

    class Default(rootModule: RootModule) : AuctionCommandDependencies {
        override val plugin: JavaPlugin by rootModule.bukkitCoreModule.plugin
        override val kyoriComponentSerializer by rootModule.bukkitCoreModule.kyoriComponentSerializer
        override val translation: Translation by rootModule.coreModule.translation
        override val router: GuiRouter by Provider {
            rootModule.auctionGuiModule.router
        }
        override val encoder: ObjectEncoder by Provider {
            rootModule.bukkitCoreModule.encoder.value
        }
        override val scope: CoroutineScope by rootModule.coreModule.scope
        override val dispatchers: KotlinDispatchers = rootModule.coreModule.dispatchers
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            rootModule.sharedDomainModule.createAuctionUseCase
        }
    }
}
