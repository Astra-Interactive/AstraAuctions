package ru.astrainteractive.astramarket.command.util

import net.kyori.adventure.audience.Audience
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer

object KyoriExt {
    fun KyoriComponentSerializer.sendMessage(string: String, audience: Audience?) {
        val component = this.toComponent(string)
        audience?.sendMessage(component)
    }
}
