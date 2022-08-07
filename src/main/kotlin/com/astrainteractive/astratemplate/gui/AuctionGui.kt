package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astralibs.utils.ReflectionUtil
import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.sqldatabase.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class AuctionGui(_playerMenuUtility: AstraPlayerMenuUtility) : AbstractAuctionGui(_playerMenuUtility) {

    private val itemsInGui: List<Auction>
        get() = viewModel.auctionList.value

    override fun setMenuItems() {
        super.setMenuItems()
        inventory.setItem(backButtonIndex - 1, expiredButton)

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = ReflectionUtil.deserializeItem<ItemStack>(auctionItem.item, auctionItem.time).apply {
                val meta = itemMeta!!
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                lore.add(Translation.leftButton)
                lore.add(Translation.middleClick)
                lore.add(Translation.rightButton)
                lore.add(
                    Translation.auctionBy.replace(
                        "%player_owner%",
                        Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid))?.name ?: "NULL"
                    )
                )
                lore.add(Translation.auctionCreatedAgo.replace("%time%", getTimeFormatted(auctionItem.time)))
                lore.add(Translation.auctionPrice.replace("%price%", auctionItem.price.toString()))

                meta.lore = lore
                setItemMeta(meta)
            }
            inventory.setItem(i, itemStack)
        }
    }

    override fun onExpiredOpenClicked() {
        AsyncHelper.launch {
            ExpiredAuctionGui(playerMenuUtility).open()
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent, manager: EventManager) = Unit
}
