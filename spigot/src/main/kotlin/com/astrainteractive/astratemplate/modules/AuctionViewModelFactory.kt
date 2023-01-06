package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.IValue
import ru.astrainteractive.astralibs.di.getValue

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : IValue<AuctionViewModel>() {
    private val dataSource by DataSourceModule
    override fun initializer(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}