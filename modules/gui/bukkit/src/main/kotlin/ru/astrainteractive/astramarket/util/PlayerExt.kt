package ru.astrainteractive.astramarket.util

import org.bukkit.entity.Player

fun Player.playSound(sound: String) {
    playSound(
        location,
        sound,
        1f,
        1f
    )
}
