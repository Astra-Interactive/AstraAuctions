package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astratemplate.api.entities.Auction
import com.astrainteractive.astratemplate.utils.*
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import java.util.*

class AuctionGui(player: Player) : AbstractAuctionGui(player) {

    private val itemsInGui: List<Auction>
        get() = viewModel.auctionList.value

    override fun setMenuItems() {
        super.setMenuItems()
        inventory.setItem(backPageButton.index - 1, expiredButton)

        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = Serializer.fromByteArray<ItemStack>(auctionItem.item).apply {
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
        PluginScope.launch {
            ExpiredAuctionGui(playerMenuUtility.player).open()
        }
    }

}
