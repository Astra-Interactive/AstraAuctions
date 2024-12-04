package ru.astrainteractive.astramarket.market.data.bridge

import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID

interface PlayerInteractionBridge {

    fun sendTranslationMessage(uuid: UUID, message: () -> StringDesc)

    fun broadcast(string: StringDesc)

    fun playSound(uuid: UUID, sound: () -> String)
}
