package com.astrainteractive.astratemplate.utils.config

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.getHEXString
import com.astrainteractive.astratemplate.AstraAuctions
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
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
    )

    data class Sounds(
        val click: String = "ui.button.click",
        val fail: String = "entity.villager.no",
        val drop: String = "entity.player.burp",
        val sold: String = "block.note_block.chime"
    )

    data class Buttons(
        val back: Button = Button(Material.IRON_DOOR.name),
        val previous: Button = Button(Material.PAPER.name),
        val next: Button = Button(Material.PAPER.name),
        val sort: Button = Button(Material.SUNFLOWER.name)
    )

    data class Button(
        val material: String,
        val customModelData: Int = 0
    ) {
        fun toItemStack() = ItemStack(Material.getMaterial(material) ?: Material.PAPER).apply {
            val meta = itemMeta
            meta.setCustomModelData(customModelData)
            itemMeta = meta
        }
    }

    companion object {
        fun load(): AuctionConfig {
            val config =
                AstraYamlParser.parser.fileConfigurationToClass<AuctionConfig>(AstraAuctions.empireFiles.configFile.getConfig())
                    ?: AuctionConfig()

            println(AstraAuctions.empireFiles.configFile.getConfig().getString("buttons.back.text"))
            println(config)
            return config
        }

    }
}
