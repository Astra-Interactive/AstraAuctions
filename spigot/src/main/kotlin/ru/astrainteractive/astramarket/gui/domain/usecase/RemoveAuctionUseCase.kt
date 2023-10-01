package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.itemStack
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.playSound
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import java.util.UUID

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
interface RemoveAuctionUseCase : UseCase.Parametrized<RemoveAuctionUseCase.Params, Boolean> {
    data class Params(
        val auction: ru.astrainteractive.astramarket.api.market.dto.AuctionDTO,
        val player: Player
    )
}

internal class RemoveAuctionUseCaseImpl(
    private val dataSource: ru.astrainteractive.astramarket.api.market.AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val serializer: Serializer
) : RemoveAuctionUseCase {

    override suspend operator fun invoke(input: RemoveAuctionUseCase.Params): Boolean {
        val (_auction, player) = input

        val auction = dataSource.fetchAuction(_auction.id) ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            player.sendMessage(translation.notAuctionOwner)
            return false
        }
        val item = auction.itemStack(serializer)
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.inventoryFull)
            return false
        }
        val result = dataSource.deleteAuction(auction)
        return if (result != null) {
            player.sendMessage(translation.auctionDeleted)
            player.inventory.addItem(item)
            true
        } else {
            player.sendMessage(translation.unexpectedError)
            false
        }
    }
}
