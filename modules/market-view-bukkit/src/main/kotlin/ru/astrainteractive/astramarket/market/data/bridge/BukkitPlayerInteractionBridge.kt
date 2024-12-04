package ru.astrainteractive.astramarket.market.data.bridge

import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID

internal class BukkitPlayerInteractionBridge(
    private val stringSerializer: KyoriComponentSerializer
) : PlayerInteractionBridge {
    override fun sendTranslationMessage(uuid: UUID, message: () -> StringDesc) {
        val component = stringSerializer.toComponent(message.invoke())
        Bukkit.getPlayer(uuid)?.sendMessage(component)
    }

    override fun broadcast(string: StringDesc) {
        val component = stringSerializer.toComponent(string)
        Bukkit.broadcast(component)
    }

    override fun playSound(uuid: UUID, sound: () -> String) {
        Bukkit.getPlayer(uuid)?.let { player ->
            player.playSound(player.location, sound.invoke(), 1f, 1f)
        }
    }
}
