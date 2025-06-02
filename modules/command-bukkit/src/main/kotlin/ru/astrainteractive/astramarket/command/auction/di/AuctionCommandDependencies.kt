package ru.astrainteractive.astramarket.command.auction.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.market.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal interface AuctionCommandDependencies {
    val plugin: JavaPlugin
    val kyoriComponentSerializer: KyoriComponentSerializer
    val translation: Translation
    val router: GuiRouter
    val itemStackEncoder: ItemStackEncoder
    val scope: CoroutineScope
    val dispatchers: KotlinDispatchers
    val limitedIoDispatcher: CoroutineDispatcher
    val createAuctionUseCase: CreateAuctionUseCase

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        routerModule: RouterModule,
        marketViewModule: MarketViewModule
    ) : AuctionCommandDependencies {
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val kyoriComponentSerializer by bukkitCoreModule.kyoriComponentSerializer
        override val translation: Translation by coreModule.translationKrate
        override val router: GuiRouter = routerModule.router
        override val itemStackEncoder = bukkitCoreModule.itemStackEncoder
        override val scope: CoroutineScope = coreModule.scope
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val createAuctionUseCase = marketViewModule.marketViewDomainModule.createAuctionUseCase
        override val limitedIoDispatcher: CoroutineDispatcher = dispatchers.IO.limitedParallelism(1)
    }
}
