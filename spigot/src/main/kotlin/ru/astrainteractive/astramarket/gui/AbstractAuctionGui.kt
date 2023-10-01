package ru.astrainteractive.astramarket.gui

import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.playSound
import ru.astrainteractive.astramarket.util.setDisplayName
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player,
    protected val config: AuctionConfig,
    protected val translation: Translation,
    protected val dispatchers: BukkitDispatchers,
    protected val auctionSortTranslationMapping: AuctionSortTranslationMapping
) : PaginatedMenu() {

    override val playerHolder = DefaultPlayerHolder(player)
    protected val clickListener = MenuClickListener()

    override var menuTitle: String = translation.title
    override val menuSize: MenuSize = MenuSize.XL
    abstract val viewModel: AuctionViewModel

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

    protected val guiMap = listOf(
        "${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.BO}",
        "${GuiKey.PR}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.NE}",
        "${GuiKey.BO}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.AI}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}${GuiKey.BO}",
        "${GuiKey.BO}${GuiKey.EM}${GuiKey.BA}${GuiKey.EM}${GuiKey.EM}${GuiKey.AU}${GuiKey.FI}${GuiKey.EM}${GuiKey.BO}",
    ).flatMap { it.map { it.toChar() } }

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
            itemStack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        }
    }

    override val backPageButton = buildSlot(GuiKey.BA) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.back.toItemStack().apply { setDisplayName(translation.back) }
            click = Click {
                onCloseClicked()
            }
        }
    }
    override val nextPageButton = buildSlot(GuiKey.NE) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.next.toItemStack().apply {
                setDisplayName(translation.next)
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
                setDisplayName(translation.prev)
            }
            click = Click {
                onPrevPageClicked()
            }
        }
    }

    val expiredButton = buildSlot(GuiKey.AU) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.expired.toItemStack().apply { setDisplayName(translation.expired) }
            click = Click {
                onExpiredOpenClicked()
            }
        }
    }
    val aaucButton = buildSlot(GuiKey.AU) { i ->
        InventorySlot.Builder {
            index = i
            itemStack = config.buttons.aauc.toItemStack().apply { setDisplayName(translation.aauc) }
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
                    val sortDesc = auctionSortTranslationMapping.translate(viewModel.sortType)
                    setDisplayName("${translation.sort} $sortDesc")
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
        get() = viewModel.maxItemsAmount

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
        viewModel.onSortButtonClicked(isRightClick)
        sortButton.setInventoryButton()
    }

    open fun onCloseClicked() {
        playerHolder.player.closeInventory()
        playerHolder.player.playSound(config.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        componentScope.launch(dispatchers.IO) {
            val result = viewModel.onAuctionItemClicked(i, clickType)
            if (result) {
                playerHolder.player.playSound(config.sounds.sold)
            } else {
                playerHolder.player.playSound(config.sounds.fail)
            }
        }
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

    fun getTimeFormatted(sec: Long): String {
        val time = System.currentTimeMillis().minus(sec)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return translation.timeAgoFormat
            .replace("%days%", days.toString())
            .replace("%hours%", hours.toString())
            .replace("%minutes%", minutes.toString())
    }

    open fun setMenuItems() {
        inventory.clear()
        clickListener.clearClickListener()
        setManageButtons(clickListener)
        borderButtons.forEach { it.setInventoryButton() }
        sortButton.also(clickListener::remember).setInventoryButton()
    }

    override fun onCreated() {
        playerHolder.player.playSound(config.sounds.open)
        viewModel.auctionList.collectOn(dispatchers.IO) {
            setMenuItems()
        }
    }
}
