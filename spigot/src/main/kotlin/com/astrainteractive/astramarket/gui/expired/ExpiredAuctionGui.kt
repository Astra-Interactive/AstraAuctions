package com.astrainteractive.astramarket.gui.expired

import com.astrainteractive.astramarket.api.market.dto.AuctionDTO
import com.astrainteractive.astramarket.gui.AbstractAuctionGui
import com.astrainteractive.astramarket.gui.AuctionViewModel
import com.astrainteractive.astramarket.gui.di.factory.AuctionGuiFactory
import com.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import java.util.UUID

class ExpiredAuctionGui(
    player: Player,
    override val viewModel: AuctionViewModel,
    override val config: AuctionConfig,
    override val translation: Translation,
    override val dispatchers: BukkitDispatchers,
    override val auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val auctionGuiFactory: AuctionGuiFactory,
    private val serializer: Serializer
) : AbstractAuctionGui(player) {

    override var menuTitle: String = translation.expiredTitle

    private val itemsInGui: List<AuctionDTO>
        get() = viewModel.auctionList.value

    override fun onExpiredOpenClicked() {
        componentScope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(playerHolder.player, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }

    override fun setMenuItems() {
        super.setMenuItems()
        aaucButton.also(clickListener::remember).setInventoryButton()
        for (i in 0 until maxItemsPerPage) {
            val index = maxItemsPerPage * page + i
            val auctionItem = itemsInGui.getOrNull(index) ?: continue

            InventorySlot.Builder {
                this.index = i
                click = Click {
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
