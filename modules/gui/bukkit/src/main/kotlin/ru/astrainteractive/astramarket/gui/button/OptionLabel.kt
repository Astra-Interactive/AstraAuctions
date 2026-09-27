package ru.astrainteractive.astramarket.gui.button

import ru.astrainteractive.astralibs.localization.text.LocalizedText
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext

/** The color of an option in a button's lore; it is joined with the option's text, so the text takes it. */
internal fun ButtonContext.optionColor(isSelected: Boolean): LocalizedText {
    return if (isSelected) pluginTranslation.menu.selectedColor else pluginTranslation.menu.unselectedColor
}

/** A sort option in lore: colored as selected or not, the selected one followed by its direction arrow. */
internal fun ButtonContext.sortOption(label: LocalizedText, isSelected: Boolean, isAsc: Boolean): LocalizedText {
    val coloredLabel = optionColor(isSelected).concat(label)
    if (!isSelected) return coloredLabel
    val symbol = if (isAsc) pluginTranslation.menu.sort.ascendingArrow else pluginTranslation.menu.sort.descendingArrow
    return coloredLabel.concat(symbol)
}
