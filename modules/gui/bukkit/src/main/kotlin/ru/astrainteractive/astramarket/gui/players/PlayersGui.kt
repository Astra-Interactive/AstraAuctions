package ru.astrainteractive.astramarket.gui.players

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.menu.core.setInventorySlot
import ru.astrainteractive.astralibs.menu.inventory.api.InventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.paginator.api.DefaultPaginator
import ru.astrainteractive.astralibs.menu.paginator.api.context
import ru.astrainteractive.astralibs.menu.paginator.api.openNextPage
import ru.astrainteractive.astralibs.menu.paginator.api.openPrevPage
import ru.astrainteractive.astralibs.menu.paginator.model.indexOfSlot
import ru.astrainteractive.astralibs.menu.paginator.model.isFirstPage
import ru.astrainteractive.astralibs.menu.paginator.model.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.gui.button.back
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.button.filterExpired
import ru.astrainteractive.astramarket.gui.button.nextPage
import ru.astrainteractive.astramarket.gui.button.playerItem
import ru.astrainteractive.astramarket.gui.button.playersSort
import ru.astrainteractive.astramarket.gui.button.prevPage
import ru.astrainteractive.astramarket.gui.button.slotsType
import ru.astrainteractive.astramarket.gui.layout.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.layout.DefaultAuctionInventoryLayoutFactory
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.util.closeInventory
import ru.astrainteractive.astramarket.gui.util.playSound
import ru.astrainteractive.astramarket.players.presentation.PlayersMarketComponent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class PlayersGui(
    configKrate: CachedKrate<PluginConfig>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
    private val inventoryOwner: OnlineKPlayer,
    private val buttonContext: ButtonContext,
    private val dispatchers: KotlinDispatchers,
    private val playersMarketComponent: PlayersMarketComponent,
    private val router: GuiRouter,
) : InventoryMenu(),
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val config by configKrate
    private val translation by translationKrate

    override val inventorySize: InventorySize = InventorySize.XL

    override val childComponents = listOf(playersMarketComponent)
    override val menuScope = CoroutineFeature
        .Default(dispatchers.Main)
        .withTimings()
    private val inventoryMap by lazy {
        DefaultAuctionInventoryLayoutFactory.create(config.auction.useCompactDesign)
    }

    override val title: Component = translation.menu.market.component

    private val paginator = DefaultPaginator(
        maxItemsPerPage = inventoryMap.count(AuctionSlotKey.AUCTION_ITEM)
    )

    private val prevPageButton: InventorySlot
        get() = buttonContext.prevPage(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.PREV_PAGE),
            click = {
                inventoryOwner.playSound(config.sounds.open)
                paginator.openPrevPage()
            }
        )

    private val nextPageButton: InventorySlot
        get() = buttonContext.nextPage(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.NEXT_PAGE),
            click = {
                inventoryOwner.playSound(config.sounds.open)
                paginator.openNextPage()
            }
        )

    private val sortButton: InventorySlot
        get() = buttonContext.playersSort(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.SORT),
            sortType = playersMarketComponent.model.value.sort,
            click = {
                inventoryOwner.playSound(config.sounds.open)
                playersMarketComponent.onSortButtonClicked(it.isRightClick)
            }
        )

    private val expiredButton: InventorySlot
        get() = buttonContext.filterExpired(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.FILTER_EXPIRED),
            isExpired = playersMarketComponent.model.value.isExpired,
            click = {
                inventoryOwner.playSound(config.sounds.open)
                paginator.openPage(0)
                playersMarketComponent.toggleExpired()
            }
        )

    private val allSlots: InventorySlot
        get() = buttonContext.slotsType(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.DISPLAY_TYPE),
            isGroupedByPlayers = true,
            click = {
                val route = GuiRouter.Route.Slots(
                    inventoryOwner = inventoryOwner,
                    isExpired = playersMarketComponent.model.value.isExpired,
                    targetPlayerUUID = null
                )
                router.navigate(route)
            }
        )

    private val closeButton: InventorySlot
        get() = buttonContext.back(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.BACK),
            click = { inventoryOwner.closeInventory() }
        )

    private val slots: List<InventorySlot>
        get() {
            var itemIndex = 0
            val isExpired = playersMarketComponent.model.value.isExpired
            return inventoryMap.mapSlotsNotNull(AuctionSlotKey.AUCTION_ITEM) { slotIndex ->
                val index = paginator.context.indexOfSlot(itemIndex)
                itemIndex++
                val items = playersMarketComponent.model
                    .value
                    .playersAndSlots
                    .filter { it.slots.any { slot -> slot.expired == isExpired } }
                    .getOrNull(index) ?: return@mapSlotsNotNull null
                buttonContext.playerItem(
                    playerAndSlots = items,
                    index = slotIndex,
                    isExpired = playersMarketComponent.model.value.isExpired,
                    click = {
                        val route = GuiRouter.Route.Slots(
                            inventoryOwner = inventoryOwner,
                            isExpired = playersMarketComponent.model.value.isExpired,
                            targetPlayerUUID = items.minecraftUUID
                        )
                        router.navigate(route)
                    },
                )
            }
        }

    private fun updatePageContext(model: PlayersMarketComponent.Model) {
        paginator.update { paginatorContext ->
            paginatorContext.copy(
                maxItems = model.playersAndSlots
                    .filter { it.slots.any { slot -> slot.expired == model.isExpired } }
                    .size
            )
        }
    }

    override fun onInventoryClickEvent(e: InventoryClickEvent) {
        super.onInventoryClickEvent(e)
        e.isCancelled = true
    }

    override fun onInventoryOpenEvent(e: InventoryOpenEvent) {
        playersMarketComponent.model
            .onEach { model -> updatePageContext(model) }
            .onEach { withContext(dispatchers.Main) { render() } }
            .launchIn(menuScope)
    }

    override fun render() {
        super.render()
        if (!paginator.context.isFirstPage) setInventorySlot(prevPageButton)
        if (!paginator.context.isLastPage) setInventorySlot(nextPageButton)
        setInventorySlot(sortButton)
        setInventorySlot(expiredButton)
        setInventorySlot(closeButton)
        setInventorySlot(allSlots)
        setInventorySlot(slots)
    }
}
