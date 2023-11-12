package ru.astrainteractive.astramarket.presentation.base

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.playSound
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.setDisplayName
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.toItemStack
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player,
    protected val config: AuctionConfig,
    protected val translation: Translation,
    protected val dispatchers: BukkitDispatchers,
    protected val auctionSortTranslationMapping: AuctionSortTranslationMapping,
    protected val stringSerializer: KyoriComponentSerializer
) : PaginatedMenu() {

    override val playerHolder = DefaultPlayerHolder(player)
    protected val clickListener = MenuClickListener()

    override var menuTitle: Component = stringSerializer.toComponent(translation.menu.title)
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

    protected inline fun buildSlot(char: Char, transform: (index: Int) -> InventorySlot): InventorySlot {
        val i = guiMap.indexOf(char)
        if (i == -1) {
            error("Could not find $char in inventory map")
        }
        return transform.invoke(i)
    }

    val borderButtons: List<InventorySlot> = buildSlots(GuiKey.BO) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.border.toItemStack().apply { setDisplayName(" ") }
        }
    }

    override val backPageButton = buildSlot(GuiKey.BA) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.back.toItemStack().apply {
                setDisplayName(
                    stringSerializer.toComponent(translation.menu.back)
                )
            }
            click = Click {
                onCloseClicked()
            }
        }
    }
    override val nextPageButton = buildSlot(GuiKey.NE) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.next.toItemStack().apply {
                setDisplayName(stringSerializer.toComponent(translation.menu.next))
            }
            click = Click {
                onNextPageClicked()
            }
        }
    }

    override val prevPageButton = buildSlot(GuiKey.PR) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.previous.toItemStack().apply {
                setDisplayName(stringSerializer.toComponent(translation.menu.prev))
            }
            click = Click {
                onPrevPageClicked()
            }
        }
    }

    val expiredButton = buildSlot(GuiKey.AU) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.expired.toItemStack().apply {
                setDisplayName(
                    stringSerializer.toComponent(translation.menu.expired)
                )
            }
            click = Click {
                onExpiredOpenClicked()
            }
        }
    }
    val aaucButton = buildSlot(GuiKey.AU) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.aauc.toItemStack().apply {
                setDisplayName(
                    stringSerializer.toComponent(translation.menu.aauc)
                )
            }
            click = Click {
                onExpiredOpenClicked()
            }
        }
    }

    val sortButton: InventorySlot
        get() = buildSlot(GuiKey.FI) { i ->
            InventorySlot.Builder {
                index = i
                itemStack = config.buttons.sort.toItemStack().apply {
                    val sortDesc = auctionSortTranslationMapping.translate(auctionComponent.model.value.sortType)
                    setDisplayName(stringSerializer.toComponent("${translation.menu.sort} $sortDesc"))
                }
                click = Click {
                    showPage(0)
                    onSortButtonClicked(it.isRightClick)
                }
            }
        }

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
        e.isCancelled = true
        if (e.clickedInventory?.holder !is Menu) return
        clickListener.onClick(e)
    }

    override fun onPageChanged() {
        setMenuItems()
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

    open fun setMenuItems() {
        inventory.clear()
        clickListener.clearClickListener()
        setManageButtons(clickListener)
        borderButtons.forEach { it.setInventorySlot() }
        sortButton.also(clickListener::remember).setInventorySlot()
    }

    override fun onCreated() {
        playerHolder.player.playSound(config.sounds.open)
        auctionComponent.model.collectOn(dispatchers.IO) {
            setMenuItems()
        }
    }
}
