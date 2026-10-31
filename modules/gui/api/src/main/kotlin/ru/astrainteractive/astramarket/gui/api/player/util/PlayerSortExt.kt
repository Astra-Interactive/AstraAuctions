package ru.astrainteractive.astramarket.gui.api.player.util

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort

fun PlayerSort.toTranslation(translation: PluginTranslation): LocalizedText {
    return when (this) {
        is PlayerSort.Name -> translation.menu.sort.byName
        is PlayerSort.Auctions -> translation.menu.sort.byAmount
    }
}
