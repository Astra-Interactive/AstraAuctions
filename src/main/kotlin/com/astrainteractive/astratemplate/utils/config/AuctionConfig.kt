package com.astrainteractive.astratemplate.utils.config

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astratemplate.AstraMarket
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


data class AuctionConfig(
    val auction: Auction = Auction(),
    val sounds: Sounds = Sounds(),
    val buttons: Buttons = Buttons()
) {
    data class Auction(
        val maxAuctionPerPlayer: Int = 5,
        val minPrice: Int = 10,
        val maxPrice: Int = 1000000,
        val taxPercent: Int = 0,
        val announce: Boolean = true,
        val maxTime:Long = 20L//1*24*60*60*1000
    )

    data class Sounds(
        val open: String = "ui.button.click",
        val close: String = "ui.button.click",
        val click: String = "ui.button.click",
        val fail: String = "entity.villager.no",
        val success: String = "block.note_block.chime",
        val sold: String = "block.note_block.chime"
    )

    data class Buttons(
        val back: Button = Button(Material.IRON_DOOR.name),
        val previous: Button = Button(Material.PAPER.name),
        val next: Button = Button(Material.PAPER.name),
        val sort: Button = Button(Material.SUNFLOWER.name),
        val aauc: Button = Button(Material.DIAMOND.name),
        val expired: Button = Button(Material.EMERALD.name)
    )

    data class Button(
        val material: String,
        val customModelData: Int = 0
    ) {
        fun toItemStack() = ItemStack(Material.getMaterial(material.uppercase()) ?: Material.PAPER).apply {
            val meta = itemMeta!!
            meta.setCustomModelData(customModelData)
            itemMeta = meta
        }
    }

    companion object {
        fun load(): AuctionConfig {
            val c = AstraMarket.empireFiles.configFile.getConfig()
            val config =
                AstraYamlParser.fileConfigurationToClass<AuctionConfig>(c)
                    ?: AuctionConfig()
            return config
        }

    }
}
