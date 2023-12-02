package ru.astrainteractive.astramarket.presentation.router.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.data.PlayerInteractionBridge
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.domain.usecase.SortAuctionsUseCase
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.DefaultAuctionComponent
import ru.astrainteractive.astramarket.presentation.di.AuctionComponentDependencies

class AuctionComponentFactory(
    private val sharedDomainModule: SharedDomainModule,
    private val dispatchers: BukkitDispatchers,
    private val auctionsAPI: AuctionsAPI,
    private val config: AuctionConfig,
    private val playerInteractionBridge: PlayerInteractionBridge,
    private val sortAuctionsUseCase: SortAuctionsUseCase
) {

    fun create(player: Player, isExpired: Boolean): AuctionComponent {
        return DefaultAuctionComponent(
            player.uniqueId,
            isExpired,
            dependencies = object : AuctionComponentDependencies {
                override val auctionBuyUseCase = sharedDomainModule.auctionBuyUseCase
                override val expireAuctionUseCase = sharedDomainModule.expireAuctionUseCase
                override val removeAuctionUseCase = sharedDomainModule.removeAuctionUseCase
                override val dispatchers = this@AuctionComponentFactory.dispatchers
                override val auctionsAPI = this@AuctionComponentFactory.auctionsAPI
                override val config = this@AuctionComponentFactory.config
                override val playerInteractionBridge = this@AuctionComponentFactory.playerInteractionBridge
                override val sortAuctionsUseCase: SortAuctionsUseCase = this@AuctionComponentFactory.sortAuctionsUseCase
            },
        )
    }
}
