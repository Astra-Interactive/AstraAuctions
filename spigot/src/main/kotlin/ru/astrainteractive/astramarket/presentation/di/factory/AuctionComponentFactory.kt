package ru.astrainteractive.astramarket.presentation.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.domain.data.PlayerInteraction
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.domain.util.AuctionSorter
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.DefaultAuctionComponent

class AuctionComponentFactory(
    private val sharedDomainModule: SharedDomainModule,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val config: AuctionConfig,
    private val playerInteraction: PlayerInteraction,
    private val auctionSorter: AuctionSorter
) {
    fun create(player: Player, isExpired: Boolean): AuctionComponent {
        return DefaultAuctionComponent(
            player.uniqueId,
            isExpired,
            auctionBuyUseCase = sharedDomainModule.auctionBuyUseCase,
            expireAuctionUseCase = sharedDomainModule.expireAuctionUseCase,
            removeAuctionUseCase = sharedDomainModule.removeAuctionUseCase,
            dispatchers = dispatchers,
            auctionsAPI = auctionsAPI,
            config = config,
            playerInteraction = playerInteraction,
            auctionSorter = auctionSorter
        )
    }
}
