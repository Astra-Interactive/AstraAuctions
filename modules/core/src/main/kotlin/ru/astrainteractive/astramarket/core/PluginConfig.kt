package ru.astrainteractive.astramarket.core

import kotlinx.serialization.Serializable

@Serializable
data class PluginConfig(
    val auction: Auction = Auction(),
    val sounds: Sounds = Sounds(),
    val buttons: Buttons = Buttons(),
    val connection: Connection = Connection()
) {
    @Serializable
    class Connection(
        val sqlite: Boolean = true,
        val mysql: MySqlConnection? = MySqlConnection()
    ) {
        @Serializable
        class MySqlConnection(
            val database: String = "SAMPLE_DATABASE",
            val ip: String = "127.0.0.1",
            val port: Int = 8080,
            val username: String = "USERNAME",
            val password: String = "PASSWORD",
            val sessionVariables: List<String> = listOf("autoReconnect=true", "useSSL=false")
        )
    }

    @Serializable
    data class Auction(
        val useCompactDesign: Boolean = true,
        val maxAuctionPerPlayer: Int = 5,
        val minPrice: Int = 10,
        val maxPrice: Int = 1000000,
        val taxPercent: Int = 0,
        val announce: Boolean = true,
        val maxTime: Long = 20L // 1*24*60*60*1000
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
        val back: Button = Button("IRON_DOOR"),
        val previous: Button = Button("PAPER"),
        val next: Button = Button("PAPER"),
        val sort: Button = Button("SUNFLOWER"),
        val aauc: Button = Button("DIAMOND"),
        val expired: Button = Button("EMERALD"),
        val border: Button = Button("BLACK_STAINED_GLASS_PANE")
    )

    @Serializable
    data class Button(
        val material: String,
        val customModelData: Int = 0
    )
}
