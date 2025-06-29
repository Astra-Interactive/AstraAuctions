package ru.astrainteractive.astramarket.market.domain.model

sealed interface AuctionSort {
    val isAsc: Boolean

    data class Date(override val isAsc: Boolean) : AuctionSort
    data class Material(override val isAsc: Boolean) : AuctionSort
    data class Name(override val isAsc: Boolean) : AuctionSort
    data class Price(override val isAsc: Boolean) : AuctionSort
    data class Player(override val isAsc: Boolean) : AuctionSort
}
