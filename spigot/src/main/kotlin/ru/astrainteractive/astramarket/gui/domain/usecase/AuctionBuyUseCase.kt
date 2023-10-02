package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.api.market.AuctionsAPI
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.itemStack
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.util.displayNameOrMaterialName
import ru.astrainteractive.astramarket.util.playSound
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to buy
 * @param player the player which will buy auction
 * @return boolean, which is true if succesfully bought
 */
interface AuctionBuyUseCase : UseCase.Parametrized<AuctionBuyUseCase.Params, Boolean> {
    class Params(
        val auction: AuctionDTO,
        val player: Player
    )
}

internal class AuctionBuyUseCaseImpl(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val economyProvider: EconomyProvider,
    private val serializer: Encoder,
    private val stringSerializer: KyoriComponentSerializer
) : AuctionBuyUseCase {

    override suspend operator fun invoke(input: AuctionBuyUseCase.Params): Boolean {
        val receivedAuction = input.auction
        val player = input.player
        val auction = dataSource.fetchAuction(receivedAuction.id) ?: return false
        if (auction.minecraftUuid == player.uniqueId.toString()) {
            stringSerializer.sendMessage(translation.auction.ownerCantBeBuyer, player)
            return false
        }
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        val item = auction.itemStack(serializer)

        if (player.inventory.firstEmpty() == -1) {
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.auction.inventoryFull, player)
            return false
        }
        var vaultResponse = economyProvider.takeMoney(player.uniqueId, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.auction.notEnoughMoney, player)
            return false
        }
        vaultResponse = economyProvider.addMoney(owner.uniqueId, auction.price.toDouble())
        if (!vaultResponse) {
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.auction.failedToPay, player)
            economyProvider.addMoney(player.uniqueId, auction.price.toDouble())
            return false
        }

        val result = dataSource.deleteAuction(auction)
        if (result != null) {
            player.inventory.addItem(item)
            player.playSound(config.sounds.sold)
            stringSerializer.sendMessage(
                translation.auction.notifyUserBuy.replace(
                    "%player_owner%",
                    owner.name ?: "${ChatColor.MAGIC}NULL"
                ).replace("%price%", auction.price.toString()).replace("%item%", item.displayNameOrMaterialName()),
                player
            )
            stringSerializer.sendMessage(
                translation.auction.notifyOwnerUserBuy.replace("%player%", player.name)
                    .replace("%item%", item.displayNameOrMaterialName()).replace("%price%", auction.price.toString()),
                owner.player
            )
        } else {
            economyProvider.addMoney(player.uniqueId, auction.price.toDouble())
            economyProvider.takeMoney(owner.uniqueId, auction.price.toDouble())
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.general.dbError, player)
        }
        return result != null
    }
}
