package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.IFactory
import ru.astrainteractive.astralibs.di.getValue

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : IFactory<AuctionViewModel> {
    private val dataSource by DataSourceModule
    override fun provide(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}