package ru.astrainteractive.astramarket.players.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.players.model.PlayerSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

interface PlayerSortTranslationMapping {
    fun translate(playerSort: PlayerSort): StringDesc.Raw
}

internal class PlayerSortTranslationMappingImpl(
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) : PlayerSortTranslationMapping {
    private val translation by pluginTranslationKrate

    override fun translate(playerSort: PlayerSort): StringDesc.Raw = when (playerSort) {
        PlayerSort.NAME_ASC -> translation.auction.sortNameAsc
        PlayerSort.NAME_DESC -> translation.auction.sortName
        PlayerSort.AUCTIONS_ASC -> translation.auction.sortAmountDesc
        PlayerSort.AUCTIONS_DESC -> translation.auction.sortAmountAsc
    }
}
