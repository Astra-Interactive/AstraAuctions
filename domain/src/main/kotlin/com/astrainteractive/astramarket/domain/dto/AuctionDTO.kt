package com.astrainteractive.astramarket.domain.dto

import ru.astrainteractive.astralibs.utils.encoding.Serializer

data class AuctionDTO(
    val id: Int,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long,
    val item: Serializer.Wrapper.ByteArray,
    val price: Float,
    var expired: Boolean,
)