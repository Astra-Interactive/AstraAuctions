package ru.astrainteractive.astramarket.gui.api.player.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort

interface PlayersMarketComponent : CoroutineScope {
    val model: StateFlow<Model>

    fun toggleExpired()

    fun onSortButtonClicked(isRightClick: Boolean)

    data class Model(
        val isExpired: Boolean,
        val playersAndSlots: List<PlayerAndSlots>,
        val sort: PlayerSort
    )
}
