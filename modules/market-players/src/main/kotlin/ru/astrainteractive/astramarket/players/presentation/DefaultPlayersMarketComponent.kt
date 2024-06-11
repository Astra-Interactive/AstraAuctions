package ru.astrainteractive.astramarket.players.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.players.model.PlayerSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.next
import ru.astrainteractive.klibs.mikro.core.util.prev

internal class DefaultPlayersMarketComponent(
    private val marketApi: MarketApi,
    private val dispatchers: KotlinDispatchers,
    isExpired: Boolean
) : PlayersMarketComponent, AsyncComponent() {
    override val model = MutableStateFlow(PlayersMarketComponent.Model(isExpired = isExpired))

    private fun sortPlayersAndSlots() {
        val items = model.value.playersAndSlots
        val sortedItems = when (model.value.sort) {
            PlayerSort.NAME_ASC -> items.sortedBy { it.minecraftUUID }
            PlayerSort.NAME_DESC -> items.sortedByDescending { it.minecraftUUID }
            PlayerSort.AUCTIONS_ASC -> items.sortedBy { it.slots.size }
            PlayerSort.AUCTIONS_DESC -> items.sortedByDescending { it.slots.size }
        }
        model.update { it.copy(playersAndSlots = sortedItems) }
    }

    private fun loadPlayersAndSlots() = launch(dispatchers.IO) {
        val slots = marketApi.findPlayersWithSlots(isExpired = model.value.isExpired)
        model.update { it.copy(playersAndSlots = slots) }
        sortPlayersAndSlots()
    }

    override fun toggleExpired() {
        model.update {
            it.copy(isExpired = !it.isExpired)
        }
        loadPlayersAndSlots()
    }

    override fun nextSort() {
        model.update {
            val sort = it.sort.next(PlayerSort.entries.toTypedArray())
            it.copy(sort = sort)
        }
        sortPlayersAndSlots()
    }

    override fun prevSort() {
        model.update {
            val sort = it.sort.prev(PlayerSort.entries.toTypedArray())
            it.copy(sort = sort)
        }
        sortPlayersAndSlots()
    }

    init {
        loadPlayersAndSlots()
    }
}
