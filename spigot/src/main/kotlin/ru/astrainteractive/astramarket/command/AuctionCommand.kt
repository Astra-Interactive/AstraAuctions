package ru.astrainteractive.astramarket.command

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.command.registerCommand
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.command.argument.FloatArgumentType
import ru.astrainteractive.astramarket.command.argument.IntArgumentType
import ru.astrainteractive.astramarket.command.argument.LabelArgument
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
    argument(0, LabelArgument("sell")).onSuccess {
        val player = sender as? Player ?: return@onSuccess
        if (!permissionManager.hasPermission(player.uniqueId, PluginPermission.Sell)) return@onSuccess

        val price = argument(1, FloatArgumentType.Optional).onFailure {
            stringSerializer.sendMessage(translation.general.wrongArgs, player)
            player.playSound(configuration.sounds.fail)
        }.resultOrNull() ?: return@onSuccess

        val inputAmount = argument(2, IntArgumentType.Optional).resultOrNull() ?: 1

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
    argument(0, LabelArgument("expired")).onSuccess {
        scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }

    if (listOf(null, "open").contains(args.getOrNull(0))) {
        scope.launch(dispatchers.IO) {
            val menu = auctionGuiFactory.create(sender, false)
            withContext(dispatchers.BukkitMain) { menu.open() }
        }
    }
}
