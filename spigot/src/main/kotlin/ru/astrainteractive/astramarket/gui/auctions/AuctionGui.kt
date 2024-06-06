package ru.astrainteractive.astramarket.gui.auctions

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.astralibs.menu.inventory.util.PageContextExt.getIndex
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.gui.base.AbstractAuctionGui
import ru.astrainteractive.astramarket.gui.base.di.AuctionGuiDependencies
import ru.astrainteractive.astramarket.gui.button.di.MenuDrawerContext
import ru.astrainteractive.astramarket.gui.invmap.AuctionInventoryMap.AuctionSlotKey
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.indexOf
import ru.astrainteractive.astramarket.gui.invmap.InventoryMapExt.withKeySlot
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.presentation.AuctionComponent

@Suppress("LongParameterList")
class AuctionGui(
    player: Player,
    override val auctionComponent: AuctionComponent,
    dependencies: AuctionGuiDependencies,
    menuDrawerContext: MenuDrawerContext
) : AbstractAuctionGui(
    player = player,
    dependencies = dependencies,
    menuDrawerContext = menuDrawerContext
) {

    private val itemsInGui: List<MarketSlot>
        get() = auctionComponent.model.value.items

    private val expiredButton: InventorySlot
        get() = expiredButtonFactory.render(
            index = inventoryMap.indexOf(AuctionSlotKey.AU),
            click = {
                val route = GuiRouter.Route.ExpiredAuctions(playerHolder.player)
                router.navigate(route)
            }
        )

    private val auctionSlots: List<InventorySlot>
        get() {
            var itemIndex = 0
            return inventoryMap.withKeySlot(AuctionSlotKey.AI) { i ->
                val index = pageContext.getIndex(itemIndex)
                itemIndex++
                val auctionItem = itemsInGui.getOrNull(index) ?: return@withKeySlot null
                auctionMarketItemButtonFactory.render(
                    index = index,
                    click = { onAuctionItemClicked(index, it.click) },
                    auctionItem = auctionItem
                )
            }
        }

    override fun render() {
        super.render()
        expiredButton.setInventorySlot()
        auctionSlots.forEach { it.setInventorySlot() }
    }

    override fun onInventoryClosed(it: InventoryCloseEvent) {
        super.onInventoryClosed(it)
        auctionComponent.close()
    }
}
