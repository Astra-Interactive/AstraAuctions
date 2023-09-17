package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.gui.AuctionViewModel
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory

interface ViewModels {
    fun auctionViewModelFactory(player: Player, expired: Boolean): Factory<AuctionViewModel>
}
