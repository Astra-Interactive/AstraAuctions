package ru.astrainteractive.astramarket.market.api

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import java.util.UUID

interface PlayerInteractionBridge {

    fun sendTranslationMessage(uuid: UUID, message: () -> LocalizableComponent)

    fun broadcast(message: LocalizableComponent)

    fun playSound(uuid: UUID, sound: () -> String)
}
