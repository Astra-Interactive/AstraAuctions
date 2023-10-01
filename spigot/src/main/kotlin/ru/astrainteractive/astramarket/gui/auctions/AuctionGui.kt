package ru.astrainteractive.astramarket.gui.auctions

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
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.AbstractAuctionGui
import ru.astrainteractive.astramarket.gui.AuctionViewModel
import ru.astrainteractive.astramarket.gui.di.factory.AuctionGuiFactory
import ru.astrainteractive.astramarket.gui.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import java.util.*

class AuctionGui(
    player: Player,
    override val viewModel: AuctionViewModel,
    override val config: AuctionConfig,
    override val translation: Translation,
    override val dispatchers: BukkitDispatchers,
    override val auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val serializer: Serializer,
    private val auctionGuiFactory: AuctionGuiFactory,
) : AbstractAuctionGui(player) {

    private val itemsInGui: List<AuctionDTO>
        get() = viewModel.auctionList.value

    override fun setMenuItems() {
        super.setMenuItems()
        expiredButton.also(clickListener::remember).setInventoryButton()
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
                    lore.add(translation.leftButton)
                    lore.add(translation.middleClick)
                    lore.add(translation.rightButton)
                    lore.add(
                        translation.auctionBy.replace(
                            "%player_owner%",
                            Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
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

    override fun onExpiredOpenClicked() {
        componentScope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(playerHolder.player, true)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}
