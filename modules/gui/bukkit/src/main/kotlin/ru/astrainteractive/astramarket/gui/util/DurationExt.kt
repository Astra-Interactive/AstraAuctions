package ru.astrainteractive.astramarket.gui.util

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astramarket.core.PluginTranslation
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Suppress("MagicNumber")
fun Duration.getTimeFormatted(format: PluginTranslation.Menu.TimeAgo): LocalizableComponent {
    val time = System.currentTimeMillis().minus(inWholeMilliseconds)
    val unit = TimeUnit.MILLISECONDS
    val days = unit.toDays(time)
    val hours = unit.toHours(time) - days * 24
    val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
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
