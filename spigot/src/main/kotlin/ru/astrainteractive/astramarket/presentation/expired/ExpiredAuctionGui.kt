package ru.astrainteractive.astramarket.presentation.expired

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.di.factory.AuctionGuiFactory
import java.util.UUID

@Suppress("LongParameterList")
class ExpiredAuctionGui(
    player: Player,
    override val auctionComponent: AuctionComponent,
    config: AuctionConfig,
    translation: Translation,
    dispatchers: BukkitDispatchers,
    auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val auctionGuiFactory: AuctionGuiFactory,
    private val serializer: Encoder,
    stringSerializer: KyoriComponentSerializer
) : AbstractAuctionGui(
    player = player,
    config = config,
    translation = translation,
    dispatchers = dispatchers,
    auctionSortTranslationMapping = auctionSortTranslationMapping,
    stringSerializer = stringSerializer
) {

    override var menuTitle: Component = stringSerializer.toComponent(translation.menu.expiredTitle)

    private val itemsInGui: List<AuctionDTO>
        get() = auctionComponent.model.value.items

    override fun onExpiredOpenClicked() {
        componentScope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(playerHolder.player, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }

    override fun setMenuItems() {
        super.setMenuItems()
        aaucButton.also(clickListener::remember).setInventorySlot()
        var itemIndex = 0
        buildSlots(GuiKey.AI) { i ->
            val index = maxItemsPerPage * page + itemIndex
            itemIndex++
            val auctionItem = itemsInGui.getOrNull(index) ?: return@buildSlots null
            InventorySlot.Builder {
                this.index = i
                click = Click {
                    onAuctionItemClicked(index, it.click)
                }
                itemStack = serializer.fromByteArray<ItemStack>(auctionItem.item).apply {
                    val meta = itemMeta!!
                    val ownerUuid = UUID.fromString(auctionItem.minecraftUuid)
                    val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                    meta.lore(
                        listOf(
                            stringSerializer.toComponent(translation.auction.rightButton),
                            stringSerializer.toComponent(
                                translation.auction.auctionBy.replace(
                                    "%player_owner%",
                                    ownerName
                                )
                            ),
                            stringSerializer.toComponent(
                                translation.auction.auctionCreatedAgo.replace(
                                    "%time%",
                                    getTimeFormatted(auctionItem.time)
                                )
                            ),
                            stringSerializer.toComponent(
                                translation.auction.auctionPrice.replace(
                                    "%price%",
                                    auctionItem.price.toString()
                                )
                            ),
                        )
                    )
                    meta.lore = lore
                    itemMeta = meta
                }
            }
        }.forEach {
            clickListener.remember(it)
            it.setInventorySlot()
        }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        auctionComponent.close()
    }
}
