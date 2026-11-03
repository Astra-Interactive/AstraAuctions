package ru.astrainteractive.astramarket.gui.bukkit.util

import org.bukkit.entity.Player

fun Player.playSound(sound: String) {
    playSound(
        location,
        sound,
        1f,
        1f
    )
}
