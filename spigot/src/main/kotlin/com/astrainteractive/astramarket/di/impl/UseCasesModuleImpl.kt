package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.di.RootModule
import com.astrainteractive.astramarket.di.UseCasesModule
import com.astrainteractive.astramarket.gui.domain.usecases.AuctionBuyUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.CreateAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.ExpireAuctionUseCase
import com.astrainteractive.astramarket.gui.domain.usecases.RemoveAuctionUseCase
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class UseCasesModuleImpl(rootModule: RootModule) : UseCasesModule {
    override val auctionBuyUseCase: AuctionBuyUseCase by Provider {
        AuctionBuyUseCase(
            dataSource = rootModule.auctionsApi.value,
            translation = rootModule.translation.value,
            config = rootModule.configuration.value,
            economyProvider = rootModule.vaultEconomyProvider.value,
            serializer = rootModule.bukkitSerializer.value
        )
    }
    override val createAuctionUseCase: CreateAuctionUseCase by Provider {
        CreateAuctionUseCase(
            dataSource = rootModule.auctionsApi.value,
            translation = rootModule.translation.value,
            config = rootModule.configuration.value,
            serializer = rootModule.bukkitSerializer.value
        )
    }
    override val expireAuctionUseCase: ExpireAuctionUseCase by Provider {
        ExpireAuctionUseCase(
            dataSource = rootModule.auctionsApi.value,
            translation = rootModule.translation.value,
            serializer = rootModule.bukkitSerializer.value
        )
    }
    override val removeAuctionUseCase: RemoveAuctionUseCase by Provider {
        RemoveAuctionUseCase(
            dataSource = rootModule.auctionsApi.value,
            translation = rootModule.translation.value,
            config = rootModule.configuration.value,
            serializer = rootModule.bukkitSerializer.value
        )
    }
}
