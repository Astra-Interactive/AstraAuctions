package ru.astrainteractive.astramarket.gui.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.gui.AuctionViewModel
import ru.astrainteractive.astramarket.gui.domain.di.GuiDomainModule

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
