package ru.astrainteractive.astramarket.command

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.Command
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.command.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.domain.usecase.CreateAuctionUseCase
import ru.astrainteractive.astramarket.plugin.PluginPermission
import ru.astrainteractive.astramarket.presentation.util.ItemStackExt.playSound
import kotlin.math.max
import kotlin.math.min

fun CommandManager.amarketCommand() = plugin.registerCommand("amarket") {
    val sender = this.sender
    if (sender !is Player) {
        stringSerializer.sendMessage(translation.general.onlyForPlayers, sender)
        return@registerCommand
    }

    if (!permissionManager.hasPermission(sender.uniqueId, PluginPermission.Amarket)) {
        stringSerializer.sendMessage(translation.general.noPermissions, sender)
        return@registerCommand
    }
    when (args.getOrNull(0)) {
        "sell" -> sellCommand.invoke(this)
        "expired" -> scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }

        "open", null -> scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}

private val CommandManager.sellCommand: Command.() -> Unit
    get() = command@{
        val player = sender as? Player ?: return@command
        if (!permissionManager.hasPermission(player.uniqueId, PluginPermission.Sell)) return@command

        val price = argument(1) {
            it?.toFloatOrNull()
        }.onFailure {
            stringSerializer.sendMessage(translation.general.wrongArgs, player)
            player.playSound(configuration.sounds.fail)
        }.successOrNull()?.value ?: return@command

        val inputAmount = argument(2) {
            it.toIntOrNull() ?: 1
        }.successOrNull()?.value ?: 1
        val item = player.inventory.itemInMainHand
        val clonedItem = item.clone().apply {
            this.amount = max(min(item.amount, inputAmount), 1)
        }
        val encodedItem = encoder.toByteArray(clonedItem)
        scope.launch(dispatchers.IO) {
            val auctionDTO = AuctionDTO(
                id = -1,
                discordId = null,
                minecraftUuid = player.uniqueId.toString(),
                time = System.currentTimeMillis(),
                item = encodedItem,
                price = price,
                expired = false
            )
            val param = CreateAuctionUseCase.Params(
                auctionDTO = auctionDTO,
                playerUUID = player.uniqueId,
            )
            val result = createAuctionUseCase.invoke(param)
            if (result) item.amount -= inputAmount
        }
    }
