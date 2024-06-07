package ru.astrainteractive.astramarket.players.mapping

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astramarket.core.Translation
import ru.astrainteractive.astramarket.players.model.PlayerSort

interface PlayerSortTranslationMapping {
    fun translate(playerSort: PlayerSort): StringDesc.Raw
}

internal class PlayerSortTranslationMappingImpl(
    private val translation: Translation
) : PlayerSortTranslationMapping {
    override fun translate(playerSort: PlayerSort): StringDesc.Raw = when (playerSort) {
        PlayerSort.NAME_ASC -> translation.auction.sortNameAsc
        PlayerSort.NAME_DESC -> translation.auction.sortNameDesc
        PlayerSort.AUCTIONS_ASC -> translation.auction.sortAmountDesc
        PlayerSort.AUCTIONS_DESC -> translation.auction.sortAmountAsc
    }
}
