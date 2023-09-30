package com.astrainteractive.astramarket.gui.di.factory

import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import com.astrainteractive.astramarket.gui.auctions.AuctionGui
import com.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import com.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer

class AuctionGuiFactory(
    private val auctionViewModelFactory: AuctionViewModelFactory,
    private val config: AuctionConfig,
    private val translation: Translation,
    private val dispatchers: BukkitDispatchers,
    private val auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val serializer: Serializer
) {
    fun create(player: Player, isExpired: Boolean): AbstractAuctionGui {
        return if (isExpired) {
            ExpiredAuctionGui(
                player = player,
                viewModel = auctionViewModelFactory.create(
                    player = player,
                    isExpired = isExpired
                ),
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = auctionSortTranslationMapping,
                serializer = serializer,
                auctionGuiFactory = this,
            )
        } else {
            AuctionGui(
                player = player,
                viewModel = auctionViewModelFactory.create(
                    player = player,
                    isExpired = isExpired
                ),
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = auctionSortTranslationMapping,
                serializer = serializer,
                auctionGuiFactory = this,
            )
        }
    }
}
