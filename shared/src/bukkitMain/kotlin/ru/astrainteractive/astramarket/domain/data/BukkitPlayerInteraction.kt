package ru.astrainteractive.astramarket.domain.data

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import java.util.UUID

class BukkitPlayerInteraction(
    private val stringSerializer: KyoriComponentSerializer
) : PlayerInteraction {
    override fun sendTranslationMessage(uuid: UUID, message: String) {
        val component = stringSerializer.toComponent(message)
        Bukkit.getPlayer(uuid)?.sendMessage(component)
    }

    override fun sendTranslationMessage(uuid: UUID, message: () -> String) {
        sendTranslationMessage(uuid, message.invoke())
    }

    override fun broadcast(string: String) {
        val component = stringSerializer.toComponent(string)
        Bukkit.broadcast(component)
    }

    override fun playSound(uuid: UUID, sound: String) {
        Bukkit.getPlayer(uuid)?.let { player ->
            player.playSound(player.location, sound, 1f, 1f)
        }
    }

    override fun playSound(uuid: UUID, sound: () -> String) {
        playSound(uuid, sound.invoke())
    }
}
