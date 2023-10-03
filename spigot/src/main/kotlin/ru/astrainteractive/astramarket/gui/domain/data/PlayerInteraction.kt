package ru.astrainteractive.astramarket.gui.domain.data

import java.util.UUID

interface PlayerInteraction {

    fun sendTranslationMessage(uuid: UUID, message: String)

    fun sendTranslationMessage(uuid: UUID, message: () -> String)

    fun broadcast(string: String)

    fun playSound(uuid: UUID, sound: String)

    fun playSound(uuid: UUID, sound: () -> String)
}
