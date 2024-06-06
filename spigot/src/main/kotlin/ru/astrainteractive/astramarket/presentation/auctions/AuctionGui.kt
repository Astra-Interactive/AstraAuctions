package ru.astrainteractive.astramarket.presentation.auctions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.presentation.AuctionComponent
import ru.astrainteractive.astramarket.presentation.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.presentation.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.presentation.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.presentation.invmap.InventoryMapExt.withKeySlot
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
        inventoryMap.withKeySlot(AuctionSlotKey.AI) { i ->
            val index = pageContext.getIndex(itemIndex)
            itemIndex++
            val auctionItem = itemsInGui.getOrNull(index) ?: return@withKeySlot null
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(objectEncoder.fromByteArray<ItemStack>(auctionItem.item))
                .editMeta {
                    listOf(
                        translation.auction.leftButton.let(kyoriComponentSerializer::toComponent),
                        translation.auction.middleClick.let(kyoriComponentSerializer::toComponent),
                        translation.auction.rightButton.let(kyoriComponentSerializer::toComponent),
                        translation.auction.auctionBy.replace(
                            "%player_owner%",
                            Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.minecraftUuid)).name ?: "NULL"
                        ).let(kyoriComponentSerializer::toComponent),
                        translation.auction.auctionCreatedAgo.replace(
                            "%time%",
                            getTimeFormatted(auctionItem.time).raw
                        ).let(kyoriComponentSerializer::toComponent),
                        translation.auction.auctionPrice.replace(
                            "%price%",
                            auctionItem.price.toString()
                        ).let(kyoriComponentSerializer::toComponent),
                    ).run(::lore)
                }
                .setOnClickListener { onAuctionItemClicked(index, it.click) }
                .build()
        }.forEach { it.setInventorySlot() }
    }

    override fun onInventoryClosed(it: InventoryCloseEvent) {
        super.onInventoryClosed(it)
        auctionComponent.close()
    }

    override fun onExpiredOpenClicked() {
        val route = GuiRouter.Route.ExpiredAuctions(playerHolder.player)
        router.navigate(route)
    }
}
