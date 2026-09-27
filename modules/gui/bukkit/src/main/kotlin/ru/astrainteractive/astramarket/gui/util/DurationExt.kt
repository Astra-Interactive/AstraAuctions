package ru.astrainteractive.astramarket.gui.util

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Suppress("MagicNumber")
fun Duration.getTimeFormatted(
    formatDHM: LocalizedText,
    formatHM: LocalizedText,
    formatM: LocalizedText
): LocalizableComponent {
    val time = System.currentTimeMillis().minus(inWholeMilliseconds)
    val unit = TimeUnit.MILLISECONDS
    val days = unit.toDays(time)
    val hours = unit.toHours(time) - days * 24
    val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
    val format = when {
        days == 0L && hours == 0L -> formatM
        days == 0L -> formatHM
        else -> formatDHM
    }
    return format.replaceAll(
        PlaceholderReplacement.plain("%days%", days.toString()),
        PlaceholderReplacement.plain("%hours%", hours.toString()),
        PlaceholderReplacement.plain("%minutes%", minutes.toString())
    )
}
