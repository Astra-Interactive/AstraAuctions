package ru.astrainteractive.astramarket.players.component

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import ru.astrainteractive.astramarket.data.dao.market.util.findPlayersWithSlots
import ru.astrainteractive.astramarket.players.model.PlayerSort
import ru.astrainteractive.astramarket.util.nextAfter
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.sortedBy

internal class DefaultPlayersMarketComponent(
    private val marketRepository: MarketRepository,
    private val dispatchers: KotlinDispatchers,
    isExpired: Boolean
) : PlayersMarketComponent,
    CoroutineFeature by CoroutineFeature.IO.withTimings() {
    override val model = MutableStateFlow(
        PlayersMarketComponent.Model(
            isExpired = isExpired,
            playersAndSlots = emptyList(),
            sort = PlayerSort.Name(false)
        )
    )

    private fun sortPlayersAndSlots() {
        val items = model.value.playersAndSlots
        val sortedItems = when (val sort = model.value.sort) {
            is PlayerSort.Auctions -> items.sortedBy(sort.isAsc) { playerAndSlots -> playerAndSlots.slots.size }
            is PlayerSort.Name -> items.sortedBy(sort.isAsc, PlayerAndSlots::minecraftUsername)
        }
        model.update { state -> state.copy(playersAndSlots = sortedItems) }
    }

    private fun loadPlayersAndSlots() {
        launch(dispatchers.IO) {
            val slots = marketRepository.findPlayersWithSlots(isExpired = model.value.isExpired)
                .getOrDefault(emptyList())
            model.update { state -> state.copy(playersAndSlots = slots) }
            sortPlayersAndSlots()
        }
    }

    override fun toggleExpired() {
        model.update { state ->
            state.copy(isExpired = !state.isExpired)
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
        val newSortType = sorts.nextAfter(model.value.sort, backwards = isRightClick)

        model.update { state ->
            state.copy(sort = newSortType)
        }
        sortPlayersAndSlots()
    }

    init {
        loadPlayersAndSlots()
    }
}
