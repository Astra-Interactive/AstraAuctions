package ru.astrainteractive.astramarket.gui.slots

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.invmap.DefaultAuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.countKeys
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.indexOf
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.withKeySlot
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.market.presentation.AuctionComponent

class SlotsGui(
    player: Player,
    dependencies: AuctionGuiDependencies,
    menuDrawerContext: MenuDrawerContext,
    private val auctionComponent: AuctionComponent
) : PaginatedInventoryMenu(),
    AuctionGuiDependencies by dependencies,
    KyoriComponentSerializer by dependencies.kyoriComponentSerializer,
    MenuDrawerContext by menuDrawerContext {
    override val playerHolder = DefaultPlayerHolder(player)
    override val title: Component = let {
        val playerNameComponent = auctionComponent.model.value
            .targetPlayerUUID
            ?.let(Bukkit::getOfflinePlayer)
            ?.name
            ?.let { name -> Component.text(": $name") }
            ?: Component.empty()
        translation.menu.title.component.append(playerNameComponent)
    }
    override val inventorySize: InventorySize = InventorySize.XL

    private val inventoryMap: AuctionInventoryMap
        get() = if (config.auction.useCompactDesign) DefaultAuctionInventoryMap else DefaultAuctionInventoryMap

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = inventoryMap.countKeys(AuctionSlotKey.AI),
        maxItems = 0
    )

    private val borderButtons: List<InventorySlot>
        get() = inventoryMap.withKeySlot(
            key = AuctionSlotKey.BO,
            transform = borderButtonRenderer::render
        )

    override val nextPageButton: InventorySlot
        get() = nextPageButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.NE),
            click = { onNextPageClicked() }
        )

    override val prevPageButton: InventorySlot
        get() = prevPageButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.PR),
            click = { onPrevPageClicked() }
        )

    private val sortButton: InventorySlot
        get() = auctionSortButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.FI),
            sortType = auctionComponent.model.value.sortType,
            click = {
                showPage(0)
                onSortButtonClicked(it.isRightClick)
            }
        )

    private val expiredButton: InventorySlot
        get() = expiredButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.AU),
            isExpired = true,
            click = {
                showPage(0)
                auctionComponent.toggleExpired()
            }
        )

    private val aaucButton: InventorySlot
        get() = aaucButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.AU),
            click = { auctionComponent.toggleExpired() }
        )

    private val openPlayersButton: InventorySlot
        get() = backButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.BA),
            click = {
                val route = GuiRouter.Route.Players(
                    player = playerHolder.player,
                    isExpired = auctionComponent.model.value.isExpired
                )
                router.navigate(route)
            }
        )

    private val closeButton: InventorySlot
        get() = backButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.BA),
            click = { playerHolder.player.closeInventory() }
        )

    private fun onNextPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showNextPage()
    }

    private fun onPrevPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPrevPage()
    }

    private fun onSortButtonClicked(isRightClick: Boolean) {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.onSortButtonClicked(isRightClick)
        sortButton.setInventorySlot()
    }

    private val itemSlots: List<InventorySlot>
        get() {
            var itemIndex = 0
            return inventoryMap.withKeySlot(AuctionSlotKey.AI) { slotIndex ->
                val index = pageContext.getIndex(itemIndex)
                itemIndex++
                val auctionItem = auctionComponent.model
                    .value
                    .items
                    .getOrNull(index)
                    ?: return@withKeySlot null
                expiredMarketItemButtonFactory.render(
                    auctionItem = auctionItem,
                    index = slotIndex,
                    click = { onAuctionItemClicked(index, it.click) },
                )
            }
        }

    override fun onInventoryClosed(it: InventoryCloseEvent) {
        super.onInventoryClosed(it)
        playerHolder.player.playSound(config.sounds.close)
        auctionComponent.close()
    }

    private fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        val sharedClickType = when (clickType) {
            ClickType.LEFT,
            ClickType.SHIFT_LEFT -> AuctionComponent.ClickType.LEFT

            ClickType.RIGHT,
            ClickType.SHIFT_RIGHT -> AuctionComponent.ClickType.RIGHT

            ClickType.MIDDLE -> AuctionComponent.ClickType.MIDDLE
            else -> return
        }
        auctionComponent.onAuctionItemClicked(i, sharedClickType)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun render() {
        super.render()
        when (auctionComponent.model.value.isExpired) {
            true -> aaucButton.setInventorySlot()
            false -> expiredButton.setInventorySlot()
        }
        if (!pageContext.isFirstPage) prevPageButton.setInventorySlot()
        if (!pageContext.isLastPage) nextPageButton.setInventorySlot()
        if (auctionComponent.model.value.targetPlayerUUID != null) {
            openPlayersButton.setInventorySlot()
        } else {
            closeButton.setInventorySlot()
        }
        borderButtons.forEach { it.setInventorySlot() }
        sortButton.setInventorySlot()
        itemSlots.forEach { it.setInventorySlot() }
    }

    private val drawDispatcher = dispatchers.IO.limitedParallelism(1)
    override fun onInventoryCreated() {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.model
            .onEach {
                pageContext = pageContext.copy(
                    maxItems = auctionComponent.model.value.items.size
                )
                render()
            }
            .flowOn(drawDispatcher)
            .launchIn(menuScope)
    }
}
