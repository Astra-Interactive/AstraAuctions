package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.di.RootModule
import com.astrainteractive.astramarket.di.ViewModels
import com.astrainteractive.astramarket.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.getValue

class ViewModelsImpl(
    private val rootModule: RootModule
) : ViewModels {
    override fun auctionViewModelFactory(
        player: Player,
        expired: Boolean
    ): Factory<AuctionViewModel> = Factory {
        AuctionViewModel(player, expired, AuctionGuiModuleImpl(rootModule))
    }
}
