package ru.astrainteractive.astramarket.gui.players

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isFirstPage
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.isLastPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astramarket.gui.button.aauc
import ru.astrainteractive.astramarket.gui.button.allSlots
import ru.astrainteractive.astramarket.gui.button.back
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.button.nextPage
import ru.astrainteractive.astramarket.gui.button.playerItem
import ru.astrainteractive.astramarket.gui.button.playersSort
import ru.astrainteractive.astramarket.gui.button.prevPage
import ru.astrainteractive.astramarket.gui.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.invmap.DefaultAuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.countKeys
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.indexOf
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.withKeySlot
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.players.presentation.PlayersMarketComponent

internal class PlayersGui(
    private val playersMarketComponent: PlayersMarketComponent,
    player: Player,
    dependencies: AuctionGuiDependencies,
    private val buttonContext: ButtonContext
) : PaginatedInventoryMenu(),
    AuctionGuiDependencies by dependencies,
    KyoriComponentSerializer by dependencies.kyoriComponentSerializer {
    override val inventorySize: InventorySize = InventorySize.XL

    private val inventoryMap: AuctionInventoryMap
        get() = DefaultAuctionInventoryMap

    override val title: Component = pluginTranslation.menu.market.component

    override val playerHolder = DefaultPlayerHolder(player)

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = inventoryMap.countKeys(AuctionSlotKey.AI),
        maxItems = 0
    )

    override val prevPageButton: InventorySlot
        get() = buttonContext.prevPage(
            index = inventoryMap.indexOf(AuctionSlotKey.PR),
            click = {
                playerHolder.player.playSound(config.sounds.open)
                showPrevPage()
            }
        )

    override val nextPageButton: InventorySlot
        get() = buttonContext.nextPage(
            index = inventoryMap.indexOf(AuctionSlotKey.NE),
            click = {
                playerHolder.player.playSound(config.sounds.open)
                showNextPage()
            }
        )

    private val sortButton: InventorySlot
        get() = buttonContext.playersSort(
            index = inventoryMap.indexOf(AuctionSlotKey.FI),
            sortType = playersMarketComponent.model.value.sort,
            click = {
                if (it.isRightClick) {
                    playersMarketComponent.nextSort()
                } else {
                    playersMarketComponent.prevSort()
                }
            }
        )

    private val expiredButton: InventorySlot
        get() = buttonContext.aauc(
            index = inventoryMap.indexOf(AuctionSlotKey.AU),
            isExpired = playersMarketComponent.model.value.isExpired,
            click = {
                showPage(0)
                playersMarketComponent.toggleExpired()
            }
        )

    private val allSlots: InventorySlot
        get() = buttonContext.allSlots(
            index = inventoryMap.indexOf(AuctionSlotKey.GR),
            isGroupedByPlayers = true,
            click = {
                val route = GuiRouter.Route.Slots(
                    player = playerHolder.player,
                    isExpired = playersMarketComponent.model.value.isExpired,
                    targetPlayerUUID = null
                )
                router.navigate(route)
            }
        )

    private val closeButton: InventorySlot
        get() = buttonContext.back(
            index = inventoryMap.indexOf(AuctionSlotKey.BA),
            click = { playerHolder.player.closeInventory() }
        )

    private val slots: List<InventorySlot>
        get() {
            var itemIndex = 0
            val isExpired = playersMarketComponent.model.value.isExpired
            return inventoryMap.withKeySlot(AuctionSlotKey.AI) { slotIndex ->
                val index = pageContext.getIndex(itemIndex)
                itemIndex++
                val items = playersMarketComponent.model
                    .value
                    .playersAndSlots
                    .filter { it.slots.any { slot -> slot.expired == isExpired } }
                    .getOrNull(index) ?: return@withKeySlot null
                buttonContext.playerItem(
                    playerAndSlots = items,
                    index = slotIndex,
                    isExpired = playersMarketComponent.model.value.isExpired,
                    click = {
                        val route = GuiRouter.Route.Slots(
                            player = playerHolder.player,
                            isExpired = playersMarketComponent.model.value.isExpired,
                            targetPlayerUUID = items.minecraftUUID
                        )
                        router.navigate(route)
                    },
                )
            }
        }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    private fun updatePageContext(model: PlayersMarketComponent.Model) {
        pageContext = pageContext.copy(
            maxItems = model.playersAndSlots
                .filter { it.slots.any { slot -> slot.expired == model.isExpired } }
                .size
        )
    }

    override fun render() {
        super.render()
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        sortButton.setInventorySlot()
        expiredButton.setInventorySlot()
        closeButton.setInventorySlot()
        allSlots.setInventorySlot()
        slots.forEach { it.setInventorySlot() }
    }

    override fun onInventoryCreated() {
        playersMarketComponent.model
            .onEach { model -> updatePageContext(model) }
            .onEach { render() }
            .flowOn(dispatchers.IO)
            .launchIn(menuScope)
    }
}
