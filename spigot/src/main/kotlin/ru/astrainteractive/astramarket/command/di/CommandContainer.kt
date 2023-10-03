package ru.astrainteractive.astramarket.command.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionGuiFactory
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface CommandContainer : Module {
    val translation: Translation
    val configuration: AuctionConfig
    val plugin: AstraMarket
    val scope: AsyncComponent
    val dispatchers: BukkitDispatchers
    val auctionGuiFactory: AuctionGuiFactory
    val createAuctionUseCase: CreateAuctionUseCase
    val stringSerializer: KyoriComponentSerializer
    val permissionManager: PermissionManager
    val encoder: Encoder

    class Default(
        rootModule: RootModule
    ) : CommandContainer {
        override val translation: Translation by rootModule.translation
        override val configuration: AuctionConfig by rootModule.configuration
        override val plugin: AstraMarket by rootModule.plugin
        override val scope: AsyncComponent by rootModule.scope
        override val dispatchers: BukkitDispatchers by rootModule.dispatchers

        override val auctionGuiFactory: AuctionGuiFactory by Provider {
            rootModule.auctionGuiModule.auctionGuiFactory
        }
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            rootModule.sharedDomainModule.createAuctionUseCase
        }
        override val stringSerializer: KyoriComponentSerializer by rootModule.stringSerializer
        override val permissionManager: PermissionManager by rootModule.permissionManager
        override val encoder: Encoder by rootModule.encoder
    }
}
