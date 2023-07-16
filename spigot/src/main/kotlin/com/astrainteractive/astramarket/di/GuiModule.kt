package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import org.bukkit.entity.Player
import ru.astrainteractive.klibs.kdi.Factory
import ru.astrainteractive.klibs.kdi.Module

interface GuiModule : Module {
    fun auctionGuiFactory(player: Player, isExpired: Boolean): Factory<AbstractAuctionGui>
}
