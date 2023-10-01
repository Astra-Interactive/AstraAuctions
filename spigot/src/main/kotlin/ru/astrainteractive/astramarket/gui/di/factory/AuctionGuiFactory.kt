package ru.astrainteractive.astramarket.gui.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.gui.AbstractAuctionGui
import ru.astrainteractive.astramarket.gui.auctions.AuctionGui
import ru.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.gui.expired.ExpiredAuctionGui
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation

class AuctionGuiFactory(
    private val auctionComponentFactory: AuctionComponentFactory,
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
                viewModel = auctionComponentFactory.create(
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
                viewModel = auctionComponentFactory.create(
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
