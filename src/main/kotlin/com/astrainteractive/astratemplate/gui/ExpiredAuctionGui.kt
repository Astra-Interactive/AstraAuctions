package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.menu.AstraPlayerMenuUtility
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.api.*
import com.astrainteractive.astratemplate.sqldatabase.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.event.inventory.ClickType
import java.util.*

class ExpiredAuctionGui(_playerMenuUtility: AstraPlayerMenuUtility) : AbstractAuctionGui(_playerMenuUtility) {

    override var menuName: String = Translation.expiredTitle
    override val viewModel: ViewModel = ViewModel(playerMenuUtility.player,expired = true)

    private val itemsInGui: List<Auction>
        get() = viewModel.auctionList.value


    override fun onExpiredOpenClicked() {
        AsyncHelper.launch {
            AuctionGui(playerMenuUtility).open()
        }
    }
    override fun setMenuItems() {
        super.setMenuItems()
        inventory.setItem(backButtonIndex - 1, aaucButton)
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = NMSHelper.deserializeItem(auctionItem.item,auctionItem.time).apply {
                val meta = itemMeta!!
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                lore.add(Translation.rightButton)
                lore.add(
                    Translation.auctionBy.replace(
                        "%player_owner%",
                        Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid))?.name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                    )
                )
                lore.add(Translation.auctionCreatedAgo.replace("%time%", getTimeFormatted(auctionItem.time)))
                lore.add(Translation.auctionPrice.replace("%price%", auctionItem.price.toString()))

                meta.lore = lore
                itemMeta = meta
            }
            inventory.setItem(i, itemStack)
        }
    }

}
