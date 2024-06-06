package ru.astrainteractive.astramarket.presentation.base

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
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setDisplayName
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.presentation.invmap.AuctionInventoryMap
import ru.astrainteractive.astramarket.presentation.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.presentation.invmap.DefaultAuctionInventoryMap
import ru.astrainteractive.astramarket.presentation.invmap.InventoryMapExt.countKeys
import ru.astrainteractive.astramarket.presentation.invmap.InventoryMapExt.indexOf
import ru.astrainteractive.astramarket.presentation.invmap.InventoryMapExt.withKeySlot
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.toItemStack
import java.util.concurrent.TimeUnit

class Cls(serializer: KyoriComponentSerializer) : KyoriComponentSerializer by serializer

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player,
    dependencies: AuctionGuiDependencies
) : PaginatedInventoryMenu(),
    AuctionGuiDependencies by dependencies,
    KyoriComponentSerializer by dependencies.kyoriComponentSerializer {

    override val playerHolder = DefaultPlayerHolder(player)

    override val title: Component by lazy {
        translation.menu.title.component
    }
    override val inventorySize: InventorySize = InventorySize.XL
    abstract val auctionComponent: AuctionComponent

    protected val inventoryMap: AuctionInventoryMap
        get() = if (config.auction.useCompactDesign) DefaultAuctionInventoryMap else DefaultAuctionInventoryMap

    val borderButtons: List<InventorySlot> = inventoryMap.withKeySlot(AuctionSlotKey.BO) { i ->
        InventorySlot.Builder()
            .setIndex(i)
            .setItemStack(config.buttons.border.toItemStack())
            .setDisplayName(" ")
            .build()
    }

    override val nextPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(inventoryMap.indexOf(AuctionSlotKey.NE))
            .setItemStack(config.buttons.next.toItemStack())
            .editMeta { displayName(translation.menu.next.component) }
            .setOnClickListener { onNextPageClicked() }
            .build()

    override val prevPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(inventoryMap.indexOf(AuctionSlotKey.PR))
            .setItemStack(config.buttons.previous.toItemStack())
            .editMeta { displayName(translation.menu.prev.component) }
            .setOnClickListener { onPrevPageClicked() }
            .build()

    val expiredButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(inventoryMap.indexOf(AuctionSlotKey.AU))
            .setItemStack(config.buttons.expired.toItemStack())
            .editMeta { displayName(translation.menu.expired.component) }
            .setOnClickListener { onExpiredOpenClicked() }
            .build()

    val aaucButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(inventoryMap.indexOf(AuctionSlotKey.AU))
            .setItemStack(config.buttons.expired.toItemStack())
            .editMeta { displayName(translation.menu.aauc.component) }
            .setOnClickListener { onExpiredOpenClicked() }
            .build()

    val sortButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(inventoryMap.indexOf(AuctionSlotKey.FI))
            .setItemStack(config.buttons.expired.toItemStack())
            .editMeta {
                val sortDesc = sortTranslationMapping.translate(auctionComponent.model.value.sortType).raw
                val desc = StringDesc.Raw("${translation.menu.sort.raw} $sortDesc")
                displayName(desc.component)
            }
            .setOnClickListener {
                showPage(0)
                onSortButtonClicked(it.isRightClick)
            }
            .build()

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

    abstract fun onExpiredOpenClicked()

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    fun getTimeFormatted(sec: Long): StringDesc.Raw {
        val time = System.currentTimeMillis().minus(sec)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return translation.general.timeAgoFormat
            .replace("%days%", days.toString())
            .replace("%hours%", hours.toString())
            .replace("%minutes%", minutes.toString())
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
