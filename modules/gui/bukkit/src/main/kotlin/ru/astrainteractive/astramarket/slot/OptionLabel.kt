package ru.astrainteractive.astramarket.slot

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.context.ButtonContext

internal fun ButtonContext.optionColor(isSelected: Boolean): LocalizedText {
    return if (isSelected) pluginTranslation.menu.selectedColor else pluginTranslation.menu.unselectedColor
}

internal fun ButtonContext.sortOption(label: LocalizedText, isSelected: Boolean, isAsc: Boolean): LocalizedText {
    val coloredLabel = optionColor(isSelected).concat(label)
    if (!isSelected) return coloredLabel
    val symbol = if (isAsc) pluginTranslation.menu.sort.ascendingArrow else pluginTranslation.menu.sort.descendingArrow
    return coloredLabel.concat(symbol)
}
