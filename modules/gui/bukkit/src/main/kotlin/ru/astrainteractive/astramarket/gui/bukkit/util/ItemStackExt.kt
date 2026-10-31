@file:Suppress("Filename", "UtilFileName")

package ru.astrainteractive.astramarket.gui.bukkit.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig

fun PluginConfig.Button.toItemStack(): ItemStack {
    val type = Material.getMaterial(material.uppercase()) ?: Material.PAPER
    return ItemStack(type).apply {
        val meta = itemMeta ?: error("ItemStack with material $type didn't have itemMeta")
        meta.setCustomModelData(customModelData)
        itemMeta = meta
    }
}
