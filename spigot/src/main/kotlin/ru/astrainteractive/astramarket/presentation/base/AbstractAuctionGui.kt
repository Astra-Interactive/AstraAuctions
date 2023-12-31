package ru.astrainteractive.astramarket.presentation.base

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.editMeta
import ru.astrainteractive.astralibs.menu.menu.setDisplayName
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.toItemStack
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player,
    dependencies: AuctionGuiDependencies
) : PaginatedMenu(), AuctionGuiDependencies by dependencies {

    override val playerHolder = DefaultPlayerHolder(player)

    override var menuTitle: Component = with(translationContext) { translation.menu.title.toComponent() }
    override val menuSize: MenuSize = MenuSize.XL
    abstract val auctionComponent: AuctionComponent

    protected object GuiKey {
        // Border
        const val BO = 'A'

        // Prev
        const val PR = 'B'

        // Next
        const val NE = 'C'

        // Auction/Expired
        const val AU = 'D'

        // Back
        const val BA = 'E'

        // Filter
        const val FI = 'F'

        // Auction item
        const val AI = 'G'

        // Empty
        const val EM = "x"
    }

    private val guiDefaultMap = listOf(
        "${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}",
        "${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}",
        "${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}",
        "${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}",
        "${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}",
        "${GuiKey.PR}${GuiKey.EM}${GuiKey.EM}${GuiKey.AU}${GuiKey.BA}${GuiKey.FI}${GuiKey.EM}${GuiKey.EM}${GuiKey.NE}",
    ).flatMap { it.map { it } }

    private val guiCompactMap = listOf(
        "${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.BO}",
        "${GuiKey.PR}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.NE}",
        "${GuiKey.BO}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.EM}${GuiKey.BA}${GuiKey.EM}${GuiKey.EM}${GuiKey.AU}${GuiKey.FI}${GuiKey.EM}${GuiKey.BO}",
    ).flatMap { it.map { it } }
    protected val guiMap = if (config.auction.useCompactDesign) guiCompactMap else guiDefaultMap

    protected inline fun buildSlots(char: Char, transform: (index: Int) -> InventorySlot?): List<InventorySlot> {
        return guiMap.mapIndexed { i, c ->
            if (c != char) {
                return@mapIndexed null
            } else {
                transform.invoke(i)
            }
        }.filterNotNull()
    }

    private fun getSlotIndexByChar(char: Char): Int {
        val i = guiMap.indexOf(char)
        if (i == -1) {
            error("Could not find $char in inventory map")
        }
        return i
    }

    val borderButtons: List<InventorySlot> = buildSlots(GuiKey.BO) { i ->
        InventorySlot.Builder()
            .setIndex(i)
            .setItemStack(config.buttons.border.toItemStack())
            .setDisplayName(" ")
            .build()
    }

    override val backPageButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.BA))
        .setItemStack(config.buttons.back.toItemStack())
        .editMeta { with(translationContext) { displayName(translation.menu.back.toComponent()) } }
        .setOnClickListener { onCloseClicked() }
        .build()

    override val nextPageButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.NE))
        .setItemStack(config.buttons.next.toItemStack())
        .editMeta { with(translationContext) { displayName(translation.menu.next.toComponent()) } }
        .setOnClickListener { onNextPageClicked() }
        .build()

    override val prevPageButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.PR))
        .setItemStack(config.buttons.previous.toItemStack())
        .editMeta { with(translationContext) { displayName(translation.menu.prev.toComponent()) } }
        .setOnClickListener { onPrevPageClicked() }
        .build()

    val expiredButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.AU))
        .setItemStack(config.buttons.expired.toItemStack())
        .editMeta { with(translationContext) { displayName(translation.menu.expired.toComponent()) } }
        .setOnClickListener { onExpiredOpenClicked() }
        .build()

    val aaucButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.AU))
        .setItemStack(config.buttons.expired.toItemStack())
        .editMeta { with(translationContext) { displayName(translation.menu.aauc.toComponent()) } }
        .setOnClickListener { onExpiredOpenClicked() }
        .build()

    val sortButton = InventorySlot.Builder()
        .setIndex(getSlotIndexByChar(GuiKey.FI))
        .setItemStack(config.buttons.expired.toItemStack())
        .editMeta {
            val sortDesc = sortTranslationMapping.translate(auctionComponent.model.value.sortType).raw
            val desc = StringDesc.Raw("${translation.menu.sort.raw} $sortDesc")
            with(translationContext) { displayName(desc.toComponent()) }
        }
        .setOnClickListener {
            showPage(0)
            onSortButtonClicked(it.isRightClick)
        }
        .build()

    override val maxItemsPerPage: Int
        get() = guiMap.count { it == GuiKey.AI }

    override var page: Int = 0

    override val maxItemsAmount: Int
        get() = auctionComponent.model.value.maxItemsAmount

    open fun onNextPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPage(page + 1)
    }

    open fun onPrevPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPage(page - 1)
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

    override fun onPageChanged() {
        render()
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

    override fun onCreated() {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.model
            .onEach { render() }
            .flowOn(dispatchers.IO)
            .launchIn(componentScope)
    }
}
