package com.astrainteractive.astramarket.gui

import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import com.astrainteractive.astramarket.util.playSound
import com.astrainteractive.astramarket.util.setDisplayName
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.menu.clicker.MenuClickListener
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventoryButton
import ru.astrainteractive.astralibs.menu.menu.Menu
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
abstract class AbstractAuctionGui(
    player: Player
) : PaginatedMenu() {
    private val config by RootModuleImpl.configuration
    protected val translation by RootModuleImpl.translation
    protected val serializer by RootModuleImpl.bukkitSerializer
    protected val scope by RootModuleImpl.scope
    protected val dispatchers by RootModuleImpl.dispatchers

    override val playerHolder = DefaultPlayerHolder(player)
    protected val clickListener = MenuClickListener()

    override var menuTitle: String = translation.title
    override val menuSize: MenuSize = MenuSize.XL
    abstract val viewModel: AuctionViewModel
    override val backPageButton = ItemStackButtonBuilder {
        index = 49
        itemStack = config.buttons.back.toItemStack().apply { setDisplayName(translation.back) }
        onClick = {
            onCloseClicked()
        }
    }

    override val nextPageButton = ItemStackButtonBuilder {
        index = 53
        itemStack = config.buttons.next.toItemStack().apply { setDisplayName(translation.next) }
        onClick = {
            onNextPageClicked()
        }
    }
    override val prevPageButton = ItemStackButtonBuilder {
        index = 45
        itemStack = config.buttons.previous.toItemStack().apply { setDisplayName(translation.prev) }
        onClick = {
            onPrevPageClicked()
        }
    }
    val expiredButton = ItemStackButtonBuilder {
        index = backPageButton.index - 1
        itemStack = config.buttons.expired.toItemStack().apply { setDisplayName(translation.expired) }
        onClick = {
            onExpiredOpenClicked()
        }
    }
    val aaucButton = ItemStackButtonBuilder {
        index = backPageButton.index - 1
        itemStack = config.buttons.aauc.toItemStack().apply { setDisplayName(translation.aauc) }
        onClick = {
            onExpiredOpenClicked()
        }
    }

    val sortButton: InventoryButton
        get() = ItemStackButtonBuilder {
            index = backPageButton.index + 1
            itemStack = config.buttons.sort.toItemStack().apply {
                setDisplayName("${translation.sort} ${viewModel.sortType.desc}")
            }
            onClick = {
                onSortButtonClicked(it.isRightClick)
            }
        }

    override var maxItemsPerPage: Int = 45
    override var page: Int = 0
    override val maxItemsAmount: Int
        get() = viewModel.maxItemsAmount

    open fun onNextPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPage(page + 1)
    }

    open fun onPrevPageClicked() {
        playerHolder.player.playSound(config.sounds.open)
        showPage(page - 1)
    }

    open fun onSortButtonClicked(isRightClick: Boolean) {
        playerHolder.player.playSound(config.sounds.open)
        viewModel.onSortButtonClicked(isRightClick)
        sortButton.setInventoryButton()
    }

    open fun onCloseClicked() {
        playerHolder.player.closeInventory()
        playerHolder.player.playSound(config.sounds.close)
    }

    open fun onAuctionItemClicked(i: Int, clickType: ClickType) {
        scope.launch(dispatchers.IO) {
            val result = viewModel.onAuctionItemClicked(i, clickType)
            if (result) {
                playerHolder.player.playSound(config.sounds.sold)
            } else {
                playerHolder.player.playSound(config.sounds.fail)
            }
        }
    }

    abstract fun onExpiredOpenClicked()
    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        if (e.clickedInventory?.holder !is Menu) return
        clickListener.onClick(e)
    }

    override fun onPageChanged() {
        setMenuItems()
    }

    fun getTimeFormatted(sec: Long): String {
        val time = System.currentTimeMillis().minus(sec)
        val unit = TimeUnit.MILLISECONDS
        val days = unit.toDays(time)
        val hours = unit.toHours(time) - days * 24
        val minutes = unit.toMinutes(time) - unit.toHours(time) * 60
        return translation.timeAgoFormat
            .replace("%days%", days.toString())
            .replace("%hours%", hours.toString())
            .replace("%minutes%", minutes.toString())
    }

    open fun setMenuItems() {
        inventory.clear()
        clickListener.clearClickListener()
        setManageButtons(clickListener)
        sortButton.also(clickListener::remember).setInventoryButton()
    }

    override fun onCreated() {
        playerHolder.player.playSound(config.sounds.open)
        viewModel.auctionList.collectOn(dispatchers.IO) {
            setMenuItems()
        }
    }
}
