package ru.astrainteractive.astramarket.api.market.model

import ru.astrainteractive.astralibs.encoding.model.EncodedObject

data class MarketSlot(
    val id: Int,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long,
    val item: EncodedObject.ByteArray,
    val price: Float,
    var expired: Boolean,
)
