package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.menu.*
import java.util.concurrent.TimeUnit

open class AbstractAuctionGui(
    player: Player
) : PaginatedMenu() {
    override val playerMenuUtility: IPlayerHolder = DefaultPlayerHolder(player)


    override var menuTitle: String = Translation.title
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    open val viewModel: ViewModel = ViewModel(playerMenuUtility.player)

    override val backPageButton = object : IInventoryButton {
        override val item: ItemStack =
            AstraMarket.pluginConfig.buttons.back.toItemStack().apply { setDisplayName(Translation.back) }
        override val index: Int = 49
    }
    override val nextPageButton = object : IInventoryButton {
        override val item: ItemStack =
            AstraMarket.pluginConfig.buttons.next.toItemStack().apply { setDisplayName(Translation.next) }
        override val index: Int = 53
    }
    override val prevPageButton = object : IInventoryButton {
        override val item: ItemStack =
            AstraMarket.pluginConfig.buttons.previous.toItemStack().apply { setDisplayName(Translation.prev) }
        override val index: Int = 45
    }


    val expiredButton: ItemStack =
        AstraMarket.pluginConfig.buttons.expired.toItemStack().apply { setDisplayName(Translation.expired) }

    val aaucButton: ItemStack =
        AstraMarket.pluginConfig.buttons.aauc.toItemStack().apply { setDisplayName(Translation.aauc) }

    private val sortButton: ItemStack
        get() = AstraMarket.pluginConfig.buttons.sort.toItemStack().apply {
            setDisplayName("${Translation.sort} ${viewModel.sortType.desc}")
        }

    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    open fun onNextPageClicked() {
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        setMenuItems()
    }

    open fun onPrevPageClicked() {
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        setMenuItems()
    }

    open fun onSortButtonClicked(isRightClick: Boolean) {
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        viewModel.onSortButtonClicked(isRightClick)
        inventory.setItem(backPageButton.index + 1, sortButton)
    }

    open fun onCloseClicked() {
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        PluginScope.launch(Dispatchers.IO) {
            val result = viewModel.onAuctionItemClicked(i, clickType)
            if (result)
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.sold)
            else
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.fail)
        }
    }

    open fun onExpiredOpenClicked() {}
    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
        when (e.slot) {
            nextPageButton.index -> onNextPageClicked()
            prevPageButton.index -> onPrevPageClicked()
            (backPageButton.index + 1) -> onSortButtonClicked(e.isRightClick)
            backPageButton.index -> onCloseClicked()
            (backPageButton.index - 1) -> onExpiredOpenClicked()
            else -> onAuctionItemClicked(getIndex(e.slot), e.click)
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {

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
        return Translation.timeAgoFormat
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
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        viewModel.auctionList.collectOn {
            setMenuItems()
        }
        setMenuItems()
    }

}
