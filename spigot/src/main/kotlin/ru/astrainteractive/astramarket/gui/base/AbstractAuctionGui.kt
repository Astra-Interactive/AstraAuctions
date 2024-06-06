package ru.astrainteractive.astramarket.gui.base

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astramarket.gui.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.invmap.DefaultAuctionInventoryMap
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.countKeys
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.indexOf
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.withKeySlot
import ru.astrainteractive.astramarket.gui.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.presentation.AuctionComponent

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player,
    dependencies: AuctionGuiDependencies,
    menuDrawerContext: MenuDrawerContext
) : PaginatedInventoryMenu(),
    AuctionGuiDependencies by dependencies,
    KyoriComponentSerializer by dependencies.kyoriComponentSerializer,
    MenuDrawerContext by menuDrawerContext {

    override val playerHolder = DefaultPlayerHolder(player)

    override val title: Component by lazy {
        translation.menu.title.component
    }
    override val inventorySize: InventorySize = InventorySize.XL
    abstract val auctionComponent: AuctionComponent

    protected val inventoryMap: AuctionInventoryMap
        get() = if (config.auction.useCompactDesign) DefaultAuctionInventoryMap else DefaultAuctionInventoryMap

    val borderButtons: List<InventorySlot>
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

    val sortButton: InventorySlot
        get() = sortButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.FI),
            sortType = auctionComponent.model.value.sortType,
            click = {
                showPage(0)
                onSortButtonClicked(it.isRightClick)
            }
        )

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItemsPerPage = inventoryMap.countKeys(AuctionSlotKey.AI),
        maxItems = 0
    )

    open fun onNextPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showNextPage()
    }

    open fun onPrevPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPrevPage()
    }

    open fun onSortButtonClicked(isRightClick: Boolean) {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.onSortButtonClicked(isRightClick)
        sortButton.setInventorySlot()
    }

    open fun onCloseClicked() {
        playerHolder.player.closeInventory()
        playerHolder.player.playSound(config.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        val sharedClickType = when (clickType) {
            ClickType.LEFT, ClickType.SHIFT_LEFT -> AuctionComponent.ClickType.LEFT
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
        borderButtons.forEach { it.setInventorySlot() }
        sortButton.setInventorySlot()
    }

    override fun onInventoryCreated() {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.model
            .onEach {
                pageContext = pageContext.copy(
                    maxItems = auctionComponent.model.value.maxItemsAmount
                )
            }
            .onEach { render() }
            .flowOn(dispatchers.IO)
            .launchIn(menuScope)
    }
}
