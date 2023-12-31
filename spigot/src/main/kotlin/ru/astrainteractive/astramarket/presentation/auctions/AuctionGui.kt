package ru.astrainteractive.astramarket.presentation.auctions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.editMeta
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astramarket.api.market.dto.MarketSlot
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import java.util.UUID

@Suppress("LongParameterList")
class AuctionGui(
    player: Player,
    override val auctionComponent: AuctionComponent,
    dependencies: AuctionGuiDependencies,
) : AbstractAuctionGui(
    player = player,
    dependencies = dependencies
) {

    private val itemsInGui: List<MarketSlot>
        get() = auctionComponent.model.value.items

    override fun render() {
        super.render()
        expiredButton.setInventorySlot()
        var itemIndex = 0
        buildSlots(GuiKey.AI) { i ->
            val index = maxItemsPerPage * page + itemIndex
            itemIndex++
            val auctionItem = itemsInGui.getOrNull(index) ?: return@buildSlots null
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(serializer.fromByteArray<ItemStack>(auctionItem.item))
                .editMeta {
                    with(translationContext) {
                        listOf(
                            translation.auction.leftButton.toComponent(),
                            translation.auction.middleClick.toComponent(),
                            translation.auction.rightButton.toComponent(),
                            translation.auction.auctionBy.replace(
                                "%player_owner%",
                                Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
                            ).toComponent(),
                            translation.auction.auctionCreatedAgo.replace(
                                "%time%",
                                getTimeFormatted(auctionItem.time).raw
                            ).toComponent(),
                            translation.auction.auctionPrice.replace(
                                "%price%",
                                auctionItem.price.toString()
                            ).toComponent(),
                        ).run(::lore)
                    }
                }
                .setOnClickListener { onAuctionItemClicked(index, it.click) }
                .build()
        }.forEach { it.setInventorySlot() }
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        auctionComponent.close()
    }

    override fun onExpiredOpenClicked() {
        val route = GuiRouter.Route.ExpiredAuctions(playerHolder.player)
        router.navigate(route)
    }
}
