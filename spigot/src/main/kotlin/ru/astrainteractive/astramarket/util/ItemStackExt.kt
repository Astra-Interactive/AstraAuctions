@file:Suppress("Filename")

package ru.astrainteractive.astramarket.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astramarket.plugin.AuctionConfig

fun ItemStack.setDisplayName(name: String) {
    val meta = itemMeta!!
    meta.setDisplayName(name)
    itemMeta = meta
}
fun ItemStack.setDisplayName(component: Component) {
    val meta = itemMeta!!
    component.decorate(TextDecoration.ITALIC)
    meta.displayName(component)
    itemMeta = meta
}

fun Player.playSound(sound: String) {
    playSound(location, sound, 1f, 1f)
}

fun AuctionConfig.Button.toItemStack() = ItemStack(Material.getMaterial(material.uppercase()) ?: Material.PAPER).apply {
    val meta = itemMeta!!
    meta.setCustomModelData(customModelData)
    itemMeta = meta
}
