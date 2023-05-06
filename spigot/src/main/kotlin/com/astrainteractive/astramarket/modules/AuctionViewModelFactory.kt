package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.getValue

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : Factory<AuctionViewModel> {
    private val dataSource by Modules.auctionsApi
    override fun build(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}
