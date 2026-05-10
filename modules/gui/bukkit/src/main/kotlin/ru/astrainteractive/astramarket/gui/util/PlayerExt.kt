package ru.astrainteractive.astramarket.gui.util

import org.bukkit.entity.Player

fun Player.playSound(sound: String) {
    playSound(location, sound, 1f, 1f)
}
