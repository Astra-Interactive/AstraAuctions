package ru.astrainteractive.astramarket.api.market.model

import java.util.UUID

data class PlayerAndSlots(
    val minecraftUUID: UUID,
    val minecraftUsername: String,
    val slots: List<MarketSlot>
)
