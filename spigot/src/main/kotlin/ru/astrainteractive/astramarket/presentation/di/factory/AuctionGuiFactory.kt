package ru.astrainteractive.astramarket.presentation.di.factory

import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.auctions.AuctionGui
import ru.astrainteractive.astramarket.presentation.expired.ExpiredAuctionGui
@Suppress("LongParameterList")
class AuctionGuiFactory(
    private val auctionComponentFactory: AuctionComponentFactory,
    private val config: AuctionConfig,
    private val translation: Translation,
    private val dispatchers: BukkitDispatchers,
    private val auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val serializer: Encoder,
    private val stringSerializer: KyoriComponentSerializer
) {
    fun create(player: Player, isExpired: Boolean): AbstractAuctionGui {
        return if (isExpired) {
            ExpiredAuctionGui(
                player = player,
                auctionComponent = auctionComponentFactory.create(
                    player = player,
                    isExpired = isExpired
                ),
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = auctionSortTranslationMapping,
                serializer = serializer,
                auctionGuiFactory = this,
                stringSerializer = stringSerializer
            )
        } else {
            AuctionGui(
                player = player,
                auctionComponent = auctionComponentFactory.create(
                    player = player,
                    isExpired = isExpired
                ),
                config = config,
                translation = translation,
                dispatchers = dispatchers,
                auctionSortTranslationMapping = auctionSortTranslationMapping,
                serializer = serializer,
                auctionGuiFactory = this,
                stringSerializer = stringSerializer
            )
        }
    }
}
