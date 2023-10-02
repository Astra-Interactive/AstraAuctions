package ru.astrainteractive.astramarket.gui.di

import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.gui.di.factory.AuctionComponentFactory
import ru.astrainteractive.astramarket.gui.di.factory.AuctionGuiFactory
import ru.astrainteractive.astramarket.gui.domain.di.GuiDomainModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface AuctionGuiModule : Module {
    // Factories
    val auctionGuiFactory: AuctionGuiFactory
    val auctionComponentFactory: AuctionComponentFactory

    // modules
    val guiDomainModule: GuiDomainModule

    class Default(
        private val economyProvider: EconomyProvider,
        private val config: AuctionConfig,
        private val translation: Translation,
        private val serializer: Encoder,
        private val auctionApi: AuctionsAPI,
        private val dispatchers: BukkitDispatchers,
        private val stringSerializer: KyoriComponentSerializer,
        private val permissionManager: PermissionManager
    ) : AuctionGuiModule {
        override val guiDomainModule: GuiDomainModule by Provider {
            GuiDomainModule.Default(
                translation = translation,
                configuration = config,
                economyProvider = economyProvider,
                auctionsAPI = auctionApi,
                serializer = serializer,
                stringSerializer = stringSerializer,
                permissionManager = permissionManager
            )
        }

        override val auctionComponentFactory: AuctionComponentFactory by Provider {
            AuctionComponentFactory(
                dispatchers = dispatchers,
                auctionsAPI = auctionApi,
                serializer = serializer,
                guiDomainModule = guiDomainModule,
                config = config
            )
        }

        override val auctionGuiFactory: AuctionGuiFactory by Provider {
            AuctionGuiFactory(
                auctionComponentFactory = auctionComponentFactory,
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = guiDomainModule.auctionSortTranslationMapping,
                serializer = serializer,
                stringSerializer = stringSerializer
            )
        }
    }
}
