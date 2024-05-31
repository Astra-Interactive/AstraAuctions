package ru.astrainteractive.astramarket.presentation.expired

import net.kyori.adventure.text.Component
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
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import java.util.UUID

@Suppress("LongParameterList")
class ExpiredAuctionGui(
    player: Player,
    override val auctionComponent: AuctionComponent,
    dependencies: AuctionGuiDependencies,
) : AbstractAuctionGui(
    player = player,
    dependencies = dependencies
) {

    override val title: Component by lazy {
        translation.menu.expiredTitle.let(kyoriComponentSerializer::toComponent)
    }

    private val itemsInGui: List<MarketSlot>
        get() = auctionComponent.model.value.items

    override fun render() {
        super.render()
        aaucButton.setInventorySlot()
        var itemIndex = 0
        buildSlots(GuiKey.AI) { i ->
            val index = pageContext.getIndex(itemIndex)
            itemIndex++
            val auctionItem = itemsInGui.getOrNull(index) ?: return@buildSlots null
            InventorySlot.Builder()
                .setIndex(i)
                .setItemStack(serializer.fromByteArray<ItemStack>(auctionItem.item))
                .editMeta {
                    val ownerUuid = UUID.fromString(auctionItem.minecraftUuid)
                    val ownerName = Bukkit.getOfflinePlayer(ownerUuid).name ?: "[ДАННЫЕ УДАЛЕНЫ]"
                    listOf(
                        translation.auction.rightButton.let(kyoriComponentSerializer::toComponent),
                        translation.auction.auctionBy.replace(
                            "%player_owner%",
                            ownerName
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

    override fun onExpiredOpenClicked() {
        val route = GuiRouter.Route.Auctions(playerHolder.player)
        router.navigate(route)
    }

    override fun onInventoryClosed(it: InventoryCloseEvent) {
        super.onInventoryClosed(it)
        auctionComponent.close()
    }
}
