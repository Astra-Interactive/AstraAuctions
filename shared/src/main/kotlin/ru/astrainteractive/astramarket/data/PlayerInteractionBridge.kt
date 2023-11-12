package ru.astrainteractive.astramarket.data

import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID

interface PlayerInteractionBridge {

    fun sendTranslationMessage(uuid: UUID, message: StringDesc.Raw)

    fun sendTranslationMessage(uuid: UUID, message: () -> StringDesc.Raw)

    fun broadcast(string: StringDesc.Raw)

    fun playSound(uuid: UUID, sound: String)

    fun playSound(uuid: UUID, sound: () -> String)
}
