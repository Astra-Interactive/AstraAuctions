package ru.astrainteractive.astramarket.presentation.auctions

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.domain.mapping.AuctionSortTranslationMapping
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.router.di.factory.AuctionGuiFactory
import java.util.UUID

@Suppress("LongParameterList")
class AuctionGui(
    player: Player,
    override val auctionComponent: AuctionComponent,
    config: AuctionConfig,
    translation: Translation,
    dispatchers: BukkitDispatchers,
    auctionSortTranslationMapping: AuctionSortTranslationMapping,
    private val serializer: Encoder,
    private val auctionGuiFactory: AuctionGuiFactory,
    stringSerializer: KyoriComponentSerializer
) : AbstractAuctionGui(
    player = player,
    config = config,
    translation = translation,
    dispatchers = dispatchers,
    auctionSortTranslationMapping = auctionSortTranslationMapping,
    stringSerializer = stringSerializer
) {

    private val itemsInGui: List<MarketSlot>
        get() = auctionComponent.model.value.items

    override fun setMenuItems() {
        super.setMenuItems()
        expiredButton.also(clickListener::remember).setInventorySlot()
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
                    meta.lore(
                        listOf(
                            stringSerializer.toComponent(translation.auction.leftButton),
                            stringSerializer.toComponent(translation.auction.middleClick),
                            stringSerializer.toComponent(translation.auction.rightButton),
                            stringSerializer.toComponent(
                                translation.auction.auctionBy.replace(
                                    "%player_owner%",
                                    Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
                                )
                            ),
                            stringSerializer.toComponent(
                                translation.auction.auctionCreatedAgo.replace(
                                    "%time%",
                                    getTimeFormatted(auctionItem.time).raw
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

    override fun onExpiredOpenClicked() {
        componentScope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(playerHolder.player, true)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}
