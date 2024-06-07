package ru.astrainteractive.astramarket.gui.util

import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

object DurationExt {
    fun Duration.getTimeFormatted(format: StringDesc.Raw): StringDesc.Raw {
        val time = System.currentTimeMillis().minus(inWholeMilliseconds)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return format
            .replace("%days%", days.toString())
            .replace("%hours%", hours.toString())
            .replace("%minutes%", minutes.toString())
    }
}
