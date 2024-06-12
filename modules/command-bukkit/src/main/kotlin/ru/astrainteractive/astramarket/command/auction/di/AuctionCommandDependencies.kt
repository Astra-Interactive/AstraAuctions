package ru.astrainteractive.astramarket.command.auction.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketModule
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AuctionCommandDependencies {
    val plugin: JavaPlugin
    val kyoriComponentSerializer: KyoriComponentSerializer
    val translation: Translation
    val router: GuiRouter
    val itemStackEncoder: ItemStackEncoder
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val createAuctionUseCase: CreateAuctionUseCase

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        routerModule: RouterModule,
        marketModule: MarketModule
    ) : AuctionCommandDependencies {
        override val plugin: JavaPlugin by bukkitCoreModule.plugin
        override val kyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
        override val translation: Translation by coreModule.translation
        override val router: GuiRouter by Provider {
            routerModule.router
        }
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
        override val scope: CoroutineScope by coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            marketModule.marketDomainModule.createAuctionUseCase
        }
    }
}
