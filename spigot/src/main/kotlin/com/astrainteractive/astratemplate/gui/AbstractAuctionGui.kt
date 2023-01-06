package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.menu.*
import ru.astrainteractive.astralibs.utils.encoding.BukkitInputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.BukkitOutputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import java.util.concurrent.TimeUnit

abstract class AbstractAuctionGui(
    player: Player
) : PaginatedMenu() {

    protected val serializer = Serializer(BukkitOutputStreamProvider, BukkitInputStreamProvider)
    override val playerMenuUtility: IPlayerHolder = PlayerHolder(player)

    val translation by TranslationModule
    private val config by ConfigModule

    override var menuTitle: String = translation.title
    override val menuSize: MenuSize = MenuSize.XL
    abstract val viewModel: AuctionViewModel

    override val backPageButton = object : IInventoryButton {
        override val item: ItemStack =
            config.buttons.back.toItemStack().apply { setDisplayName(translation.back) }
        override val index: Int = 49
        override val onClick: (e: InventoryClickEvent) -> Unit = {}
    }
    override val nextPageButton = object : IInventoryButton {
        override val item: ItemStack =
            config.buttons.next.toItemStack().apply { setDisplayName(translation.next) }
        override val index: Int = 53
        override val onClick: (e: InventoryClickEvent) -> Unit = {}
    }
    override val prevPageButton = object : IInventoryButton {
        override val item: ItemStack =
            config.buttons.previous.toItemStack().apply { setDisplayName(translation.prev) }
        override val index: Int = 45
        override val onClick: (e: InventoryClickEvent) -> Unit = {}
    }


    val expiredButton: ItemStack =
        config.buttons.expired.toItemStack().apply { setDisplayName(translation.expired) }

    val aaucButton: ItemStack =
        config.buttons.aauc.toItemStack().apply { setDisplayName(translation.aauc) }

    private val sortButton: ItemStack
        get() = config.buttons.sort.toItemStack().apply {
            setDisplayName("${translation.sort} ${viewModel.sortType.desc}")
        }

    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    open fun onNextPageClicked() {
        playerMenuUtility.player.playSound(config.sounds.open)
        setMenuItems()
    }

    open fun onPrevPageClicked() {
        playerMenuUtility.player.playSound(config.sounds.open)
        setMenuItems()
    }

    open fun onSortButtonClicked(isRightClick: Boolean) {
        playerMenuUtility.player.playSound(config.sounds.open)
        viewModel.onSortButtonClicked(isRightClick)
        inventory.setItem(backPageButton.index + 1, sortButton)
    }

    open fun onCloseClicked() {
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.playSound(config.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        PluginScope.launch(Dispatchers.IO) {
            val result = viewModel.onAuctionItemClicked(i, clickType)
            if (result)
                playerMenuUtility.player.playSound(config.sounds.sold)
            else
                playerMenuUtility.player.playSound(config.sounds.fail)
        }
    }

    open fun onExpiredOpenClicked() {}
    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        handleChangePageClick(e.slot)
        when (e.slot) {
            nextPageButton.index -> onNextPageClicked()
            prevPageButton.index -> onPrevPageClicked()
            (backPageButton.index + 1) -> onSortButtonClicked(e.isRightClick)
            backPageButton.index -> onCloseClicked()
            (backPageButton.index - 1) -> onExpiredOpenClicked()
            else -> onAuctionItemClicked(getIndex(e.slot), e.click)
        }
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
        setManageButtons()
        inventory.setItem(backPageButton.index + 1, sortButton)
    }

    override fun onCreated() {
        playerMenuUtility.player.playSound(config.sounds.open)
        viewModel.auctionList.collectOn {
            setMenuItems()
        }
        setMenuItems()
    }

}
