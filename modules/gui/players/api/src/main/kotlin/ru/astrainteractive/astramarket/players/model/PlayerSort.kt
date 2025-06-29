package ru.astrainteractive.astramarket.players.model

sealed interface PlayerSort {
    val isAsc: Boolean

    data class Name(override val isAsc: Boolean) : PlayerSort
    data class Auctions(override val isAsc: Boolean) : PlayerSort
}
