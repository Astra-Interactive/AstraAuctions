package ru.astrainteractive.astramarket.market.data.bridge

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import java.util.UUID

interface PlayerInteractionBridge {

    fun sendTranslationMessage(uuid: UUID, message: () -> LocalizableComponent)

    /** Every receiver reads [message] in its own language. */
    fun broadcast(message: LocalizableComponent)

    fun playSound(uuid: UUID, sound: () -> String)
}
