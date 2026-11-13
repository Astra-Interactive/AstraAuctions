package ru.astrainteractive.astramarket.gui.bukkit.player.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort

internal fun selectPlayerSortTranslation(sort: PlayerSort, translation: PluginTranslation): LocalizedText {
    return when (sort) {
        is PlayerSort.Name -> translation.menu.sort.byName
        is PlayerSort.Auctions -> translation.menu.sort.byAmount
    }
}
