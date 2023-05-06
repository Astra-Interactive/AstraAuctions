package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import com.astrainteractive.astramarket.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.Factory
import ru.astrainteractive.astralibs.getValue

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : Factory<AuctionViewModel> {
    private val dataSource by RootModuleImpl.auctionsApi
    override fun build(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}
