package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.menu.AstraMenuSize
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.menu.PaginatedMenu
import com.astrainteractive.astratemplate.AstraAuctions
import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class AuctionGui(override val playerMenuUtility: AstraPlayerMenuUtility) : PaginatedMenu(), AsyncTask {


    override var menuName: String = Translation.instanse.title
    override val menuSize: AstraMenuSize = AstraMenuSize.XL
    private var sortType: SortType = SortType.DATE_ASC


    override val backPageButton: ItemStack =
        AstraAuctions.pluginConfig.buttons.back.toItemStack().apply { setDisplayName(Translation.instanse.back) }
    override val nextPageButton: ItemStack =
        AstraAuctions.pluginConfig.buttons.next.toItemStack().apply { setDisplayName(Translation.instanse.next) }
    override val prevPageButton: ItemStack =
        AstraAuctions.pluginConfig.buttons.previous.toItemStack().apply { setDisplayName(Translation.instanse.prev) }
    private val sortButton: ItemStack
        get() = AstraAuctions.pluginConfig.buttons.sort.toItemStack().apply {
            setDisplayName("${Translation.instanse.sort} ${sortType.desc}")
        }
    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    private var itemsInGui = AuctionAPI.sortBy(sortType)
    override val maxItemsAmount: Int = itemsInGui.size


    override fun handleMenu(e: InventoryClickEvent) {
        super.handleMenu(e)
        when (e.slot) {
            nextButtonIndex -> {
                playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.open)
                setMenuItems()
            }
            prevButtonIndex -> {
                playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.open)
                setMenuItems()
            }
            (backButtonIndex + 1) -> {
                playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.open)
                sortType = if (e.isRightClick)
                    sortType.next()
                else
                    sortType.prev()

                itemsInGui = AuctionAPI.sortBy(sortType)
                setMenuItems()
            }
            backButtonIndex -> {
                inventory.close()
                playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.close)
            }
            else -> {
                if (e.isLeftClick) {
                    val auction = itemsInGui[getIndex(e.slot)]
                    launch(Dispatchers.IO) {
                        val result = AuctionAPI.buyAuction(auction, playerMenuUtility.player)
                        if (result) {
                            updateItems()
                            itemsInGui = runBlocking { AuctionAPI.sortBy(sortType) }
                            setMenuItems()
                            playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.sold)
                        } else {
                            playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.fail)
                        }

                    }
                }

            }
        }
    }

    private fun getTimeFormatted(sec: Long): String {
        val time = System.currentTimeMillis().minus(sec)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return "${days}дн. ${hours}ч ${minutes}м назад"
    }

    override fun setMenuItems() {

        if (!isInventoryInitialized())
            return
        inventory.clear()
        addManageButtons()
        inventory.setItem(backButtonIndex + 1, sortButton)
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = ItemStack.deserializeBytes(auctionItem.item).apply {
                val meta = itemMeta
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                lore.add(Translation.instanse.leftButton)
                lore.add(Translation.instanse.rightButton)
                lore.add("${ChatColor.GRAY}Выставил: #d6a213${Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid))?.name}".HEX())
                lore.add("${ChatColor.GRAY}Время: #d6a213${getTimeFormatted(auctionItem.time)}".HEX())
                lore.add("${ChatColor.GRAY}Стоимость: #d6a213${auctionItem.price}".HEX())
                meta.lore = lore
                setItemMeta(meta)
            }
            inventory.setItem(i, itemStack)
        }
    }

    private fun updateItems() = launch {
        val result = AuctionAPI.loadAuctions() as List<Auction>
        itemsInGui = result
        setMenuItems()
    }

    init {
        playerMenuUtility.player.playSound(AstraAuctions.pluginConfig.sounds.open)
        updateItems()
    }

}
