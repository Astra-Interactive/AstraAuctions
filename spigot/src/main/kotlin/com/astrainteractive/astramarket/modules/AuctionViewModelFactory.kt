package com.astrainteractive.astramarket.modules

import com.astrainteractive.astramarket.gui.AuctionViewModel
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.di.Factory
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class AuctionViewModelFactory(
    private val player: Player,
    private val expired: Boolean = false,
) : Factory<AuctionViewModel>() {
    private val dataSource by Modules.auctionsApi
    override fun initializer(): AuctionViewModel {
        return AuctionViewModel(player, expired, dataSource)
    }
}

val event = DSLEvent.event<PlayerChunkLoadEvent> { event ->
    if (event.player.name == "MISHA")
        event.chunk.unload()
}