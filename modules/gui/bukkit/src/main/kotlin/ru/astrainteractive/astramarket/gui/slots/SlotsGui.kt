package ru.astrainteractive.astramarket.gui.slots

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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
import ru.astrainteractive.astralibs.menu.paginator.api.setMaxItems
import ru.astrainteractive.astralibs.menu.paginator.model.indexOfSlot
import ru.astrainteractive.astralibs.menu.paginator.model.isFirstPage
import ru.astrainteractive.astralibs.menu.paginator.model.isLastPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.core.PluginPermission
import ru.astrainteractive.astramarket.core.PluginTranslation
import ru.astrainteractive.astramarket.gui.button.auctionSort
import ru.astrainteractive.astramarket.gui.button.back
import ru.astrainteractive.astramarket.gui.button.border
import ru.astrainteractive.astramarket.gui.button.di.ButtonContext
import ru.astrainteractive.astramarket.gui.button.expiredSlot
import ru.astrainteractive.astramarket.gui.button.filterExpired
import ru.astrainteractive.astramarket.gui.button.nextPage
import ru.astrainteractive.astramarket.gui.button.prevPage
import ru.astrainteractive.astramarket.gui.button.slotsType
import ru.astrainteractive.astramarket.gui.layout.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.layout.DefaultAuctionInventoryLayoutFactory
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.util.closeInventory
import ru.astrainteractive.astramarket.gui.util.playSound
import ru.astrainteractive.astramarket.market.presentation.AuctionComponent
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class SlotsGui(
    configKrate: CachedKrate<PluginConfig>,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val inventoryOwner: OnlineKPlayer,
    private val router: GuiRouter,
    private val buttonContext: ButtonContext,
    private val auctionComponent: AuctionComponent,
    private val dispatchers: KotlinDispatchers,
) : InventoryMenu(),
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val config by configKrate
    private val translation by translationKrate

    override val title: Component = let {
        val playerNameComponent = auctionComponent.model.value
            .targetPlayerUUID
            ?.let(Bukkit::getOfflinePlayer)
            ?.name
            ?.let { name -> Component.text(": $name") }
            ?: Component.empty()
        translation.menu.market.component.append(playerNameComponent)
    }
    override val childComponents = listOf(auctionComponent)

    override val menuScope = CoroutineFeature
        .Default(dispatchers.Main)
        .withTimings()

    override val inventorySize: InventorySize = InventorySize.XL

    private val inventoryMap by lazy {
        DefaultAuctionInventoryLayoutFactory.create(config.auction.useCompactDesign)
    }

    private val paginator = DefaultPaginator(
        maxItemsPerPage = inventoryMap.count(AuctionSlotKey.AUCTION_ITEM)
    )

    private val borderButtons: List<InventorySlot>
        get() = inventoryMap.mapSlotsNotNull(
            key = AuctionSlotKey.BORDER,
            transform = buttonContext::border
        )

    private val nextPageButton: InventorySlot
        get() = buttonContext.nextPage(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.NEXT_PAGE),
            click = { onNextPageClicked() }
        )

    private val prevPageButton: InventorySlot
        get() = buttonContext.prevPage(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.PREV_PAGE),
            click = { onPrevPageClicked() }
        )

    private val sortButton: InventorySlot
        get() = buttonContext.auctionSort(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.SORT),
            sortType = auctionComponent.model.value.sortType,
            click = {
                paginator.openPage(0)
                onSortButtonClicked(it.isRightClick)
            }
        )

    private val expiredSlotsButton: InventorySlot
        get() = buttonContext.filterExpired(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.AUCTION_ITEM),
            isExpired = auctionComponent.model.value.isExpired,
            click = {
                this@SlotsGui.inventoryOwner.playSound(config.sounds.open)
                paginator.openPage(0)
                auctionComponent.toggleExpired()
            }
        )

    private val openPlayersButton: InventorySlot
        get() = buttonContext.back(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.BACK),
            click = {
                val route = GuiRouter.Route.Players(
                    inventoryOwner = this@SlotsGui.inventoryOwner,
                    isExpired = auctionComponent.model.value.isExpired
                )
                router.navigate(route)
            }
        )

    private val playerSlots: InventorySlot
        get() = buttonContext.slotsType(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.DISPLAY_TYPE),
            isGroupedByPlayers = false,
            click = {
                val route = GuiRouter.Route.Players(
                    inventoryOwner = this@SlotsGui.inventoryOwner,
                    isExpired = auctionComponent.model.value.isExpired
                )
                router.navigate(route)
            }
        )

    private val closeButton: InventorySlot
        get() = buttonContext.back(
            index = inventoryMap.firstIndexOf(AuctionSlotKey.BACK),
            click = { this@SlotsGui.inventoryOwner.closeInventory() }
        )

    private fun onNextPageClicked() {
        this@SlotsGui.inventoryOwner.playSound(config.sounds.open)
        paginator.openNextPage()
    }

    private fun onPrevPageClicked() {
        this@SlotsGui.inventoryOwner.playSound(config.sounds.open)
        paginator.openPrevPage()
    }

    private fun onSortButtonClicked(isRightClick: Boolean) {
        this@SlotsGui.inventoryOwner.playSound(config.sounds.open)
        auctionComponent.onSortButtonClicked(isRightClick)
        setInventorySlot(sortButton)
    }

    private val itemSlots: List<InventorySlot>
        get() {
            var itemIndex = 0
            return inventoryMap.mapSlotsNotNull(AuctionSlotKey.AUCTION_ITEM) { slotIndex ->
                val index = paginator.context.indexOfSlot(itemIndex)
                itemIndex++
                val auctionItem = auctionComponent.model
                    .value
                    .items
                    .getOrNull(index)
                    ?: return@mapSlotsNotNull null
                val permissible = this@SlotsGui.inventoryOwner
                buttonContext.expiredSlot(
                    auctionItem = auctionItem,
                    index = slotIndex,
                    click = { onAuctionItemClicked(index, it.click) },
                    isOwner = auctionItem.minecraftUuid == this@SlotsGui.inventoryOwner.uuid.toString(),
                    hasExpirePermission = permissible.hasPermission(PluginPermission.Expire),
                    hasRemovePermission = permissible.hasPermission(PluginPermission.RemoveSlot)
                )
            }
        }

    override fun onInventoryCloseEvent(e: InventoryCloseEvent) {
        super.onInventoryCloseEvent(e)
        this@SlotsGui.inventoryOwner.playSound(config.sounds.close)
        auctionComponent.cancel()
    }

    override fun onInventoryOpenEvent(e: InventoryOpenEvent) {
        this@SlotsGui.inventoryOwner.playSound(config.sounds.open)
        auctionComponent.model
            .onEach { paginator.setMaxItems(auctionComponent.model.value.items.size) }
            .onEach { withContext(dispatchers.Main) { render() } }
            .launchIn(menuScope)
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

    override fun onInventoryClickEvent(e: InventoryClickEvent) {
        super.onInventoryClickEvent(e)
        e.isCancelled = true
    }

    override fun render() {
        super.render()
        setInventorySlot(expiredSlotsButton)
        if (!paginator.context.isFirstPage) setInventorySlot(prevPageButton)
        if (!paginator.context.isLastPage) setInventorySlot(nextPageButton)
        if (auctionComponent.model.value.targetPlayerUUID != null) {
            setInventorySlot(openPlayersButton)
        } else {
            setInventorySlot(closeButton)
            setInventorySlot(playerSlots)
        }
        setInventorySlot(borderButtons)
        setInventorySlot(sortButton)
        setInventorySlot(itemSlots)
    }
}
