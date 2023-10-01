package ru.astrainteractive.astramarket.gui.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.gui.AuctionComponent
import ru.astrainteractive.astramarket.gui.DefaultAuctionComponent
import ru.astrainteractive.astramarket.gui.domain.di.GuiDomainModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig

class AuctionComponentFactory(
    private val guiDomainModule: GuiDomainModule,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val serializer: Serializer,
    private val config: AuctionConfig
) {
    fun create(player: Player, isExpired: Boolean): AuctionComponent {
        return DefaultAuctionComponent(
            player,
            isExpired,
            auctionBuyUseCase = guiDomainModule.auctionBuyUseCase,
            expireAuctionUseCase = guiDomainModule.expireAuctionUseCase,
            removeAuctionUseCase = guiDomainModule.removeAuctionUseCase,
            dispatchers = dispatchers,
            auctionsAPI = auctionsAPI,
            serializer = serializer,
            config = config
        )
    }
}
