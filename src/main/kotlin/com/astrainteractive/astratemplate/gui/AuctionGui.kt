package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import java.util.*

class AuctionGui(_playerMenuUtility: AstraPlayerMenuUtility) : AbstractAuctionGui(_playerMenuUtility), AsyncTask {

    override var itemsInGui = AuctionAPI.sortBy(sortType)

    override fun setMenuItems() {
        if (!isInventoryInitialized())
            return
        super.setMenuItems()
        inventory.setItem(backButtonIndex - 1, expiredButton)

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = NMSHelper.deserializeItem(auctionItem.item,auctionItem.time).apply {
                val meta = itemMeta!!
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                lore.add(Translation.instance.leftButton)
                lore.add(Translation.instance.middleClick)
                lore.add(Translation.instance.rightButton)
                lore.add(
                    Translation.instance.auctionBy.replace(
                        "%player_owner%",
                        Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid))?.name ?: "NULL"
                    )
                )
                lore.add(Translation.instance.auctionCreatedAgo.replace("%time%", getTimeFormatted(auctionItem.time)))
                lore.add(Translation.instance.auctionPrice.replace("%price%", auctionItem.price.toString()))

                meta.lore = lore
                setItemMeta(meta)
            }
            inventory.setItem(i, itemStack)
        }
    }
    override fun onAaucExpiredClicked() {
        launch {
            ExpiredAuctionGui(playerMenuUtility).open()
        }
    }

    override suspend fun updateItems() {
        AuctionAPI.loadAuctions() as List<Auction>
        itemsInGui = AuctionAPI.sortBy(sortType)
        setMenuItems()
    }
}
