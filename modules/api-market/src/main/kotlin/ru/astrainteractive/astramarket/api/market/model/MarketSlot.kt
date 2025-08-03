package ru.astrainteractive.astramarket.api.market.model

import ru.astrainteractive.astralibs.encoding.model.EncodedObject

data class MarketSlot(
    val id: Int,
    val minecraftUuid: String,
    val minecraftUsername: String,
    val time: Long,
    val item: String,
    val price: Float,
    var expired: Boolean,
)
