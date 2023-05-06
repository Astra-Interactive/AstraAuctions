package com.astrainteractive.astramarket.gui.expired

import com.astrainteractive.astramarket.di.AuctionViewModelFactory
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import com.astrainteractive.astramarket.gui.AuctionViewModel
import com.astrainteractive.astramarket.gui.auctions.AuctionGui
import com.astrainteractive.astramarket.utils.openSync
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import java.util.*

class ExpiredAuctionGui(player: Player) : AbstractAuctionGui(player) {

    override var menuTitle: String = translation.expiredTitle
    override val viewModel: AuctionViewModel = AuctionViewModelFactory(playerHolder.player, expired = true).build()

    private val itemsInGui: List<AuctionDTO>
        get() = viewModel.auctionList.value

    override fun onExpiredOpenClicked() {
        scope.launch(dispatchers.IO) {
            AuctionGui(playerHolder.player).openSync()
        }
    }
    override fun setMenuItems() {
        super.setMenuItems()
        aaucButton.also(clickListener::remember).setInventoryButton()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            ItemStackButtonBuilder {
                this.index = i
                onClick = {
                    onAuctionItemClicked(getIndex(it.slot), it.click)
                }
                itemStack = serializer.fromByteArray<ItemStack>(auctionItem.item).apply {
                    val meta = itemMeta!!
                    val lore = meta.lore?.toMutableList() ?: mutableListOf()
                    val ownerUuid = UUID.fromString(auctionItem.minecraftUuid)
                    val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                    lore.add(translation.rightButton)
                    lore.add(
                        translation.auctionBy.replace(
                            "%player_owner%",
                            ownerName
                        )
                    )
                    lore.add(translation.auctionCreatedAgo.replace("%time%", getTimeFormatted(auctionItem.time)))
                    lore.add(translation.auctionPrice.replace("%price%", auctionItem.price.toString()))

                    meta.lore = lore
                    itemMeta = meta
                }
            }.also(clickListener::remember).setInventoryButton()
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        viewModel.close()
    }
}
