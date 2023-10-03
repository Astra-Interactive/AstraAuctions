package ru.astrainteractive.astramarket.presentation.di

import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.domain.di.BukkitDomainModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionComponentFactory
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionGuiFactory
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface AuctionGuiModule : Module {
    // Factories
    val auctionGuiFactory: AuctionGuiFactory
    val auctionComponentFactory: AuctionComponentFactory

    @Suppress("LongParameterList")
    class Default(
        private val config: AuctionConfig,
        private val translation: Translation,
        private val serializer: Encoder,
        private val auctionApi: AuctionsAPI,
        private val dispatchers: BukkitDispatchers,
        private val stringSerializer: KyoriComponentSerializer,
        bukkitDomainModule: BukkitDomainModule,
        sharedDomainModule: SharedDomainModule
    ) : AuctionGuiModule {

        override val auctionComponentFactory: AuctionComponentFactory by Provider {
            AuctionComponentFactory(
                dispatchers = dispatchers,
                auctionsAPI = auctionApi,
                sharedDomainModule = sharedDomainModule,
                config = config,
                playerInteraction = bukkitDomainModule.playerInteraction,
                auctionSorter = bukkitDomainModule.auctionSorter
            )
        }

        override val auctionGuiFactory: AuctionGuiFactory by Provider {
            AuctionGuiFactory(
                auctionComponentFactory = auctionComponentFactory,
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = sharedDomainModule.auctionSortTranslationMapping,
                serializer = serializer,
                stringSerializer = stringSerializer
            )
        }
    }
}
