package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

open class AbstractAuctionGui(
    final override val playerMenuUtility: AstraPlayerMenuUtility
) : PaginatedMenu() {

    override val prevButtonIndex: Int = 45
    override val backButtonIndex: Int = 49
    override val nextButtonIndex: Int = 53

    override var menuName: String = Translation.title
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    open val viewModel: ViewModel = ViewModel(playerMenuUtility.player)

    override val backPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.back.toItemStack().apply { setDisplayName(Translation.back) }
    override val nextPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.next.toItemStack().apply { setDisplayName(Translation.next) }
    override val prevPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.previous.toItemStack().apply { setDisplayName(Translation.prev) }

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
        inventory.setItem(backButtonIndex + 1, sortButton)
    }

    open fun onCloseClicked() {
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        AsyncHelper.launch(Dispatchers.IO) {
            val result = viewModel.onAuctionItemClicked(i, clickType)
            if (result)
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.sold)
            else
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.fail)
        }
    }

    open fun onExpiredOpenClicked() {}

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when (e.slot) {
            nextButtonIndex -> onNextPageClicked()
            prevButtonIndex -> onPrevPageClicked()
            (backButtonIndex + 1) -> onSortButtonClicked(e.isRightClick)
            backButtonIndex -> onCloseClicked()
            (backButtonIndex - 1) -> onExpiredOpenClicked()
            else -> onAuctionItemClicked(getIndex(e.slot), e.click)
        }
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


    override fun setMenuItems() {
        inventory.clear()
        addManageButtons()
        inventory.setItem(backButtonIndex + 1, sortButton)
    }

    init {
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        AsyncHelper.launch {
            viewModel.auctionList.collectLatest {
                while (!isInventoryInitialized())
                    delay(100)
                setMenuItems()
            }
        }
    }

}
