package ru.astrainteractive.astramarket.gui.api.market.fake

import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astramarket.gui.api.market.api.PlayerInteractionBridge
import java.util.UUID

class FakePlayerInteractionBridge : PlayerInteractionBridge {
    val messages = mutableListOf<SentMessage>()
    val sounds = mutableListOf<PlayedSound>()
    val broadcasts = mutableListOf<LocalizableComponent>()

    fun messagesTo(uuid: UUID): List<LocalizableComponent> {
        return messages.filter { message -> message.receiver == uuid }.map(SentMessage::message)
    }

    fun soundsFor(uuid: UUID): List<String> {
        return sounds.filter { sound -> sound.receiver == uuid }.map(PlayedSound::sound)
    }

    override fun sendTranslationMessage(uuid: UUID, message: () -> LocalizableComponent) {
        messages += SentMessage(uuid, message.invoke())
    }

    override fun broadcast(message: LocalizableComponent) {
        broadcasts += message
    }

    override fun playSound(uuid: UUID, sound: () -> String) {
        sounds += PlayedSound(uuid, sound.invoke())
    }

    data class SentMessage(val receiver: UUID, val message: LocalizableComponent)

    data class PlayedSound(val receiver: UUID, val sound: String)
}
