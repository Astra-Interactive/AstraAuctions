@file:Suppress("Filename")

package ru.astrainteractive.astramarket.gui.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astramarket.core.PluginConfig

object ItemStackExt {
    fun Player.playSound(sound: String) {
        playSound(location, sound, 1f, 1f)
    }

    fun PluginConfig.Button.toItemStack() =
        ItemStack(Material.getMaterial(material.uppercase()) ?: Material.PAPER).apply {
            val meta = itemMeta!!
            meta.setCustomModelData(customModelData)
            itemMeta = meta
        }
}
