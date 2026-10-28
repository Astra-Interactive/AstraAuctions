package ru.astrainteractive.astramarket.market.bukkit

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.server.sendMessage
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.astramarket.market.api.PlayerInteractionBridge
import java.util.UUID

internal class BukkitPlayerInteractionBridge : PlayerInteractionBridge {
    override fun sendTranslationMessage(uuid: UUID, message: () -> LocalizableComponent) {
        Bukkit.getPlayer(uuid)?.asKAudience()?.sendMessage(message.invoke())
    }

    override fun broadcast(message: LocalizableComponent) {
        val players = Bukkit.getOnlinePlayers().map(Player::asKAudience)
        val console = Bukkit.getConsoleSender().asKAudience()
        players.plus(console).sendMessage(message)
    }

    override fun playSound(uuid: UUID, sound: () -> String) {
        Bukkit.getPlayer(uuid)?.let { player ->
            player.playSound(
                player.location,
                sound.invoke(),
                1f,
                1f
            )
        }
    }
}
