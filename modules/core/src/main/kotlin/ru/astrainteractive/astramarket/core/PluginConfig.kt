package ru.astrainteractive.astramarket.core

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

@Serializable
data class PluginConfig(
    @SerialName("auction")
    val auction: Auction = Auction(),
    @SerialName("sounds")
    val sounds: Sounds = Sounds(),
    @SerialName("buttons")
    val buttons: Buttons = Buttons(),
) {

    @Serializable
    data class Auction(
        @SerialName("use_compact_design")
        val useCompactDesign: Boolean = true,
        @SerialName("max_auction_per_player")
        val maxAuctionPerPlayer: Int = 5,
        @SerialName("min_price")
        val minPrice: Int = 10,
        @SerialName("max_price")
        val maxPrice: Int = 1000000,
        @SerialName("tax_percent")
        val taxPercent: Int = 0,
        @SerialName("announce")
        val announce: Boolean = true,
        @SerialName("max_time_seconds")
        val maxTimeSeconds: Long = 7.days.inWholeMilliseconds, // 1*24*60*60*1000
        @SerialName("currency_id")
        @YamlComment("The vault id of currency you want to use")
        val currencyId: String? = null
    )

    @Serializable
    data class Sounds(
        @SerialName("open")
        val open: String = "ui.button.click",
        @SerialName("close")
        val close: String = "ui.button.click",
        @SerialName("click")
        val click: String = "ui.button.click",
        @SerialName("fail")
        val fail: String = "entity.villager.no",
        @SerialName("success")
        val success: String = "block.note_block.chime",
        @SerialName("sold")
        val sold: String = "block.note_block.chime"
    )

    @Serializable
    data class Buttons(
        @SerialName("back")
        val back: Button = Button("REDSTONE_BLOCK"),
        @SerialName("previous")
        val previous: Button = Button("ARROW"),
        @SerialName("next")
        val next: Button = Button("ARROW"),
        @SerialName("sort")
        val sort: Button = Button("OBSERVER"),
        @SerialName("aauc")
        val filterExpired: Button = Button("COMPOSTER"),
        @SerialName("border")
        val border: Button = Button("BLACK_STAINED_GLASS_PANE"),
        @SerialName("players_slots")
        val slotsType: Button = Button("SLIME_BLOCK")
    )

    @Serializable
    data class Button(
        @SerialName("material")
        val material: String,
        @SerialName("custom_model_data")
        val customModelData: Int = 0
    )
}
