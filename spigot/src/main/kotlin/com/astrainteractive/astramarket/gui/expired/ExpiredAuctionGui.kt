package com.astrainteractive.astramarket.gui.expired

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import com.astrainteractive.astramarket.gui.AuctionViewModel
import com.astrainteractive.astramarket.gui.di.AuctionGuiModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import java.util.UUID

class ExpiredAuctionGui(
    player: Player,
    override val viewModel: AuctionViewModel,
    module: AuctionGuiModule
) : AbstractAuctionGui(player, module) {

    override var menuTitle: String = translation.expiredTitle

    private val itemsInGui: List<AuctionDTO>
        get() = viewModel.auctionList.value

    override fun onExpiredOpenClicked() {
        scope.launch(dispatchers.IO) {
            val menu = guiModule.auctionGuiFactory(playerHolder.player, false).create()
            withContext(dispatchers.BukkitMain) { menu.open() }
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
