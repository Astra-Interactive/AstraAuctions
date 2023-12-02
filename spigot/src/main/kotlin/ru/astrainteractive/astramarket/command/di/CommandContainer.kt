package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.command.auction.AuctionCommandFactory
import ru.astrainteractive.astramarket.command.auction.di.AuctionCommandDependencies
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface CommandContainer : Module {
    val translation: Translation
    val configuration: AuctionConfig
    val plugin: AstraMarket
    val scope: AsyncComponent
    val dispatchers: BukkitDispatchers
    val createAuctionUseCase: CreateAuctionUseCase
    val stringSerializer: KyoriComponentSerializer
    val encoder: Encoder
    val router: GuiRouter
    val translationContext: BukkitTranslationContext

    val auctionCommandFactory: AuctionCommandFactory

    class Default(
        rootModule: RootModule
    ) : CommandContainer {
        override val translation: Translation by rootModule.bukkitCoreModule.translation
        override val configuration: AuctionConfig by rootModule.bukkitCoreModule.configuration
        override val plugin: AstraMarket by rootModule.bukkitCoreModule.plugin
        override val scope: AsyncComponent by rootModule.bukkitCoreModule.scope
        override val dispatchers: BukkitDispatchers by rootModule.bukkitCoreModule.dispatchers

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
