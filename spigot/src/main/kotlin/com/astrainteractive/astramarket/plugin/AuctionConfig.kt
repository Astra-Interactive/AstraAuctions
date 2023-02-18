package com.astrainteractive.astramarket.plugin

import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


@Serializable
data class AuctionConfig(
    val auction: Auction = Auction(),
    val sounds: Sounds = Sounds(),
    val buttons: Buttons = Buttons(),
    val connection: Connection = Connection()
) {
    @Serializable
    class Connection(
        val sqlite: Boolean = false,
        val mysql: MySqlConnection? = null
    ) {
        @Serializable
        class MySqlConnection(
            val database: String,
            val ip: String,
            val port: Int,
            val username: String,
            val password: String,
            val sessionVariables: List<String>
        )
    }
    @Serializable
    data class Auction(
        val maxAuctionPerPlayer: Int = 5,
        val minPrice: Int = 10,
        val maxPrice: Int = 1000000,
        val taxPercent: Int = 0,
        val announce: Boolean = true,
        val maxTime:Long = 20L//1*24*60*60*1000
    )
    @Serializable
    data class Sounds(
        val open: String = "ui.button.click",
        val close: String = "ui.button.click",
        val click: String = "ui.button.click",
        val fail: String = "entity.villager.no",
        val success: String = "block.note_block.chime",
        val sold: String = "block.note_block.chime"
    )
    @Serializable
    data class Buttons(
        val back: Button = Button(Material.IRON_DOOR.name),
        val previous: Button = Button(Material.PAPER.name),
        val next: Button = Button(Material.PAPER.name),
        val sort: Button = Button(Material.SUNFLOWER.name),
        val aauc: Button = Button(Material.DIAMOND.name),
        val expired: Button = Button(Material.EMERALD.name)
    )
    @Serializable
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
}
