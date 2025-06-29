package ru.astrainteractive.astramarket.players.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astramarket.api.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.players.model.PlayerSort

interface PlayersMarketComponent {
    val model: StateFlow<Model>

    fun toggleExpired()

    fun onSortButtonClicked(isRightClick: Boolean)

    data class Model(
        val isExpired: Boolean = false,
        val playersAndSlots: List<PlayerAndSlots> = emptyList(),
        val sort: PlayerSort = PlayerSort.Name(false)
    )
}
