package ru.astrainteractive.astramarket.players.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.findPlayersWithSlots
import ru.astrainteractive.astramarket.core.util.sortedBy
import ru.astrainteractive.astramarket.players.model.PlayerSort
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class DefaultPlayersMarketComponent(
    private val marketApi: MarketApi,
    private val dispatchers: KotlinDispatchers,
    isExpired: Boolean
) : PlayersMarketComponent,
    CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO) {
    override val model = MutableStateFlow(PlayersMarketComponent.Model(isExpired = isExpired))

    private fun sortPlayersAndSlots() {
        val items = model.value.playersAndSlots
        val sortedItems = when (val sort = model.value.sort) {
            is PlayerSort.Auctions -> items.sortedBy(sort.isAsc) { it.slots.size }
            is PlayerSort.Name -> items.sortedBy(sort.isAsc) { it.minecraftUsername }
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

    override fun onSortButtonClicked(isRightClick: Boolean) {
        val sorts = listOf(
            PlayerSort.Name(false),
            PlayerSort.Name(true),
            PlayerSort.Auctions(false),
            PlayerSort.Auctions(true),
        )
        val i = sorts.indexOfFirst { sortType -> sortType == model.value.sort }
        val offset = if (isRightClick) -1 else 1

        val newSortType = if (i == -1) {
            sorts.first()
        } else {
            sorts[(i + offset) % sorts.size]
        }

        model.update {
            it.copy(sort = newSortType)
        }
        sortPlayersAndSlots()
    }

    init {
        loadPlayersAndSlots()
    }
}
