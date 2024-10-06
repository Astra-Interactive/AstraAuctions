package ru.astrainteractive.astramarket.core

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
data class PluginConfig(
    val auction: Auction = Auction(),
    val sounds: Sounds = Sounds(),
    val buttons: Buttons = Buttons(),
) {

    @Serializable
    data class Auction(
        val useCompactDesign: Boolean = true,
        val maxAuctionPerPlayer: Int = 5,
        val minPrice: Int = 10,
        val maxPrice: Int = 1000000,
        val taxPercent: Int = 0,
        val announce: Boolean = true,
        val maxTimeSeconds: Long = 7.days.inWholeMilliseconds, // 1*24*60*60*1000
        @SerialName("currency_id")
        @YamlComment("The id of currency you want to use")
        val currencyId: String? = null
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
