package ru.astrainteractive.astramarket.data

import java.util.UUID

interface PlayerInteractionBridge {

    fun sendTranslationMessage(uuid: UUID, message: String)

    fun sendTranslationMessage(uuid: UUID, message: () -> String)

    fun broadcast(string: String)

    fun playSound(uuid: UUID, sound: String)

    fun playSound(uuid: UUID, sound: () -> String)
}
