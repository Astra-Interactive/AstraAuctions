package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

open class AbstractAuctionGui(final override val playerMenuUtility: AstraPlayerMenuUtility) : PaginatedMenu(), AsyncTask {


    override var menuName: String = Translation.instance.title
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    var sortType: SortType = SortType.DATE_ASC


    override val backPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.back.toItemStack().apply { setDisplayName(Translation.instance.back) }
    override val nextPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.next.toItemStack().apply { setDisplayName(Translation.instance.next) }
    override val prevPageButton: ItemStack =
        AstraMarket.pluginConfig.buttons.previous.toItemStack().apply { setDisplayName(Translation.instance.prev) }
    val aaucButton: ItemStack =
        AstraMarket.pluginConfig.buttons.aauc.toItemStack().apply { setDisplayName(Translation.instance.aauc) }
    val expiredButton: ItemStack =
        AstraMarket.pluginConfig.buttons.expired.toItemStack().apply { setDisplayName(Translation.instance.expired) }
    private val sortButton: ItemStack
        get() = AstraMarket.pluginConfig.buttons.sort.toItemStack().apply {
            setDisplayName("${Translation.instance.sort} ${sortType.desc}")
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    open var itemsInGui = listOf<Auction>()
    override val maxItemsAmount: Int
        get() = itemsInGui.size

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
        sortType = if (isRightClick)
            sortType.next()
        else
            sortType.prev()
        sortAuction()
    }

    open fun sortAuction() {
        itemsInGui = AuctionAPI.sortBy(sortType)
        setMenuItems()
    }

    open fun onCloseClicked() {
        playerMenuUtility.player.closeInventory()
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.close)
    }

    open fun onAuctionItemClicked(auction: Auction, clickType: ClickType) {
        launch(Dispatchers.IO) {
            val result = when (clickType) {
                ClickType.LEFT -> AuctionAPI.buyAuction(auction, playerMenuUtility.player)
                ClickType.RIGHT -> AuctionAPI.removeAuction(auction, playerMenuUtility.player)
                ClickType.MIDDLE -> AuctionAPI.forceExpireAuction(playerMenuUtility.player, auction)
                else -> return@launch
            }
            if (result) {
                updateItems()
                itemsInGui = runBlocking { AuctionAPI.sortBy(sortType) }
                setMenuItems()
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.sold)
            } else
                playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.fail)
        }
    }

    open fun onAaucExpiredClicked(){}

    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when (e.slot) {
            nextButtonIndex -> onNextPageClicked()
            prevButtonIndex -> onPrevPageClicked()
            (backButtonIndex + 1) -> onSortButtonClicked(e.isRightClick)
            backButtonIndex -> onCloseClicked()
            (backButtonIndex-1)->onAaucExpiredClicked()
            else -> {
                val auction = itemsInGui[getIndex(e.slot)]
                onAuctionItemClicked(auction, e.click)
            }
        }
    }

    fun getTimeFormatted(sec: Long): String {
        val time = System.currentTimeMillis().minus(sec)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return Translation.instance.timeAgoFormat
            .replace("%days%",days.toString())
            .replace("%hours%",hours.toString())
            .replace("%minutes%", minutes.toString())
    }


    override fun setMenuItems() {
        if (!isInventoryInitialized())
            return
        inventory.clear()
        addManageButtons()
        inventory.setItem(backButtonIndex + 1, sortButton)

    }



    open suspend fun updateItems() {
        AuctionAPI.loadAuctions() as List<Auction>
        itemsInGui = AuctionAPI.sortBy(sortType)
        setMenuItems()
    }

    init {
        playerMenuUtility.player.playSound(AstraMarket.pluginConfig.sounds.open)
        launch {
            updateItems()
        }
    }

}
