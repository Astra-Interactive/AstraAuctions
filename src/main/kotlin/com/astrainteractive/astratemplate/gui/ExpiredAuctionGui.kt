package com.astrainteractive.astratemplate.gui

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astratemplate.modules.AuctionViewModelFactory
import com.astrainteractive.astratemplate.modules.TranslationModule
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.utils.encoding.BukkitInputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import java.util.*

class ExpiredAuctionGui(player: Player) : AbstractAuctionGui(player) {

    override var menuTitle: String = translation.expiredTitle
    override val viewModel: AuctionViewModel = AuctionViewModelFactory(playerMenuUtility.player,expired = true).provide()

    private val itemsInGui: List<AuctionDTO>
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

            val itemStack = Serializer.fromByteArray<ItemStack>(auctionItem.item, BukkitInputStreamProvider).apply {
                val meta = itemMeta!!
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                lore.add(translation.rightButton)
                lore.add(
                    translation.auctionBy.replace(
                        "%player_owner%",
                        Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid))?.name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                    )
                )
                lore.add(translation.auctionCreatedAgo.replace("%time%", getTimeFormatted(auctionItem.time)))
                lore.add(translation.auctionPrice.replace("%price%", auctionItem.price.toString()))

                meta.lore = lore
                itemMeta = meta
            }
            inventory.setItem(i, itemStack)
        }
    }

}
