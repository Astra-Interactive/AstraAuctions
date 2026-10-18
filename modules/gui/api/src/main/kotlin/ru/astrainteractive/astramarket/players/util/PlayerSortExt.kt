package ru.astrainteractive.astramarket.players.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.players.model.PlayerSort

fun PlayerSort.toTranslation(translation: PluginTranslation): LocalizedText = when (this) {
    is PlayerSort.Name -> translation.menu.sort.byName
    is PlayerSort.Auctions -> translation.menu.sort.byAmount
}
