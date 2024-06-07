package ru.astrainteractive.astramarket.api.market.model

import java.util.UUID

data class PlayerAndSlots(
    val minecraftUUID: UUID,
    val slots: List<MarketSlot>
)
