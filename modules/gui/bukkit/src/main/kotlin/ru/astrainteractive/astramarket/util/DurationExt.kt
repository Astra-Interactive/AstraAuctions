package ru.astrainteractive.astramarket.util

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import kotlin.time.Duration

@Suppress("MagicNumber")
fun Duration.getTimeFormatted(format: PluginTranslation.Menu.TimeAgo): LocalizableComponent {
    val days = inWholeDays
    val hours = inWholeHours - days * 24
    val minutes = inWholeMinutes - inWholeHours * 60
    val text = when {
        days == 0L && hours == 0L -> format.minutes
        days == 0L -> format.hoursMinutes
        else -> format.daysHoursMinutes
    }
    return text.replaceAll(
        PlaceholderReplacement.plain("%days%", days.toString()),
        PlaceholderReplacement.plain("%hours%", hours.toString()),
        PlaceholderReplacement.plain("%minutes%", minutes.toString())
    )
}
