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

class ExpiredAuctionGui(player: Player) : AbstractAuctionGui(player) {

    override var menuTitle: String = Translation.expiredTitle
    override val viewModel: ViewModel = ViewModel(playerMenuUtility.player,expired = true)

    private val itemsInGui: List<Auction>
        get() = viewModel.auctionList.value


    override fun onExpiredOpenClicked() {
        PluginScope.launch {
            AuctionGui(playerMenuUtility.player).open()
        }
    }
    override fun setMenuItems() {
        super.setMenuItems()
        inventory.setItem(backPageButton.index - 1, aaucButton)
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            val itemStack = Serializer.fromByteArray<ItemStack>(auctionItem.item).apply {
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
