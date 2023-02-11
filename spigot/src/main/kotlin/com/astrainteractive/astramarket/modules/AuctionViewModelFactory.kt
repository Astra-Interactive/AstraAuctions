package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.Factory
import ru.astrainteractive.astralibs.di.getValue

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : Factory<AuctionViewModel>() {
    private val dataSource by Modules.auctionsApi
    override fun initializer(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}