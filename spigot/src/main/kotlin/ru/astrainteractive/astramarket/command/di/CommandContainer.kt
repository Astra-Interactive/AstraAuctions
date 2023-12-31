package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CommandContainer : Module {
    val translation: Translation
    val configuration: PluginConfig
    val plugin: AstraMarket
    val scope: AsyncComponent
    val dispatchers: KotlinDispatchers
    val createAuctionUseCase: CreateAuctionUseCase
    val stringSerializer: KyoriComponentSerializer
    val encoder: Encoder
    val router: GuiRouter
    val translationContext: BukkitTranslationContext

    val auctionCommandFactory: AuctionCommandFactory

    class Default(
        rootModule: RootModule
    ) : CommandContainer {
        override val translation: Translation by rootModule.coreModule.translation
        override val configuration: PluginConfig by rootModule.coreModule.config
        override val plugin: AstraMarket by rootModule.bukkitCoreModule.plugin
        override val scope: AsyncComponent by rootModule.coreModule.scope
        override val dispatchers: KotlinDispatchers = rootModule.coreModule.dispatchers

        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            rootModule.sharedDomainModule.createAuctionUseCase
        }
        override val stringSerializer: KyoriComponentSerializer by rootModule.bukkitCoreModule.stringSerializer
        override val encoder: Encoder by rootModule.bukkitCoreModule.encoder
        override val router: GuiRouter = rootModule.auctionGuiModule.router
        override val translationContext: BukkitTranslationContext = rootModule.bukkitCoreModule.translationContext

        override val auctionCommandFactory: AuctionCommandFactory by lazy {
            val dependencies = AuctionCommandDependencies.Impl(rootModule)
            AuctionCommandFactory(dependencies)
        }
    }
}
