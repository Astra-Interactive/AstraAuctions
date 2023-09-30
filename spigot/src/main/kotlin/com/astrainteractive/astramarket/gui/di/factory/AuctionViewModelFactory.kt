package com.astrainteractive.astramarket.gui.di.factory

import com.astrainteractive.astramarket.api.market.AuctionsAPI
import com.astrainteractive.astramarket.gui.AuctionViewModel
import com.astrainteractive.astramarket.gui.domain.di.GuiDomainModule
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer

class AuctionViewModelFactory(
    private val guiDomainModule: GuiDomainModule,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val serializer: Serializer
) {
    fun create(player: Player, isExpired: Boolean): AuctionViewModel {
        return AuctionViewModel(
            player,
            isExpired,
            auctionBuyUseCase = guiDomainModule.auctionBuyUseCase,
            expireAuctionUseCase = guiDomainModule.expireAuctionUseCase,
            removeAuctionUseCase = guiDomainModule.removeAuctionUseCase,
            dispatchers = dispatchers,
            auctionsAPI = auctionsAPI,
            serializer = serializer
        )
    }
}
