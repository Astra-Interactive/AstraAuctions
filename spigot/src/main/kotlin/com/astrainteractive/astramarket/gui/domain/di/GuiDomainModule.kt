package com.astrainteractive.astramarket.gui.domain.di

import com.astrainteractive.astramarket.api.market.AuctionsAPI
import com.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import com.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMappingImpl
import com.astrainteractive.astramarket.gui.domain.usecases.AuctionBuyUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.AuctionBuyUseCaseImpl
import com.astrainteractive.astramarket.gui.domain.usecases.CreateAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.CreateAuctionUseCaseImpl
import com.astrainteractive.astramarket.gui.domain.usecases.ExpireAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.ExpireAuctionUseCaseImpl
import com.astrainteractive.astramarket.gui.domain.usecases.RemoveAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.RemoveAuctionUseCaseImpl
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface GuiDomainModule {
    // Mappers
    val auctionSortTranslationMapping: AuctionSortTranslationMapping

    // UseCases
    val auctionBuyUseCase: AuctionBuyUseCase
    val createAuctionUseCase: CreateAuctionUseCase
    val expireAuctionUseCase: ExpireAuctionUseCase
    val removeAuctionUseCase: RemoveAuctionUseCase

    class Default(
        auctionsAPI: AuctionsAPI,
        translation: Translation,
        configuration: AuctionConfig,
        economyProvider: EconomyProvider,
        serializer: Serializer
    ) : GuiDomainModule {
        override val auctionSortTranslationMapping: AuctionSortTranslationMapping by Provider {
            AuctionSortTranslationMappingImpl(
                translation = translation
            )
        }
        override val auctionBuyUseCase: AuctionBuyUseCase by Provider {
            AuctionBuyUseCaseImpl(
                dataSource = auctionsAPI,
                translation = translation,
                config = configuration,
                economyProvider = economyProvider,
                serializer = serializer
            )
        }
        override val createAuctionUseCase: CreateAuctionUseCase by Provider {
            CreateAuctionUseCaseImpl(
                dataSource = auctionsAPI,
                translation = translation,
                config = configuration,
                serializer = serializer
            )
        }
        override val expireAuctionUseCase: ExpireAuctionUseCase by Provider {
            ExpireAuctionUseCaseImpl(
                dataSource = auctionsAPI,
                translation = translation,
                serializer = serializer
            )
        }
        override val removeAuctionUseCase: RemoveAuctionUseCase by Provider {
            RemoveAuctionUseCaseImpl(
                dataSource = auctionsAPI,
                translation = translation,
                config = configuration,
                serializer = serializer
            )
        }
    }
}
