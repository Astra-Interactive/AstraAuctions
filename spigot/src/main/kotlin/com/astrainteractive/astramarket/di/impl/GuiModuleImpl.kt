package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.di.GuiModule
import com.astrainteractive.astramarket.di.RootModule
import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import com.astrainteractive.astramarket.gui.auctions.AuctionGui
import com.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory

class GuiModuleImpl(
    private val rootModule: RootModule
) : GuiModule {
    override fun auctionGuiFactory(player: Player, isExpired: Boolean): Factory<AbstractAuctionGui> = Factory {
        val viewModel = rootModule.viewModelsModule.auctionViewModelFactory(player, isExpired).create()
        if (isExpired) {
            ExpiredAuctionGui(
                player = player,
                viewModel = viewModel,
                module = AuctionGuiModuleImpl(rootModule)
            )
        } else {
            AuctionGui(
                player = player,
                viewModel = viewModel,
                module = AuctionGuiModuleImpl(rootModule)
            )
        }
    }
}
