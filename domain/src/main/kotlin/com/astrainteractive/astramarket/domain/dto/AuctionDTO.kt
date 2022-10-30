package com.astrainteractive.astramarket.domain.dto

data class AuctionDTO(
    val id: Long,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long,
    val item: ByteArray,
    val price: Float,
    var expired: Boolean,
)