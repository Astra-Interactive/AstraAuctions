package ru.astrainteractive.astramarket.players.mapping

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.players.model.PlayerSort
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

interface PlayerSortTranslationMapping {
    fun translate(playerSort: PlayerSort): LocalizedText
}

internal class PlayerSortTranslationMappingImpl(
    pluginTranslationKrate: CachedKrate<PluginTranslation>
) : PlayerSortTranslationMapping {
    private val translation by pluginTranslationKrate

    override fun translate(
        playerSort: PlayerSort
    ): LocalizedText = when (playerSort) {
        is PlayerSort.Name -> translation.menu.sort.byName
        is PlayerSort.Auctions -> translation.menu.sort.byAmount
    }
}
