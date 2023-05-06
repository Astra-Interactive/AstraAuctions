package com.astrainteractive.astramarket.domain.dto

import ru.astrainteractive.astralibs.encoding.IO

data class AuctionDTO(
    val id: Int,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long,
    val item: IO.ByteArray,
    val price: Float,
    var expired: Boolean,
)
