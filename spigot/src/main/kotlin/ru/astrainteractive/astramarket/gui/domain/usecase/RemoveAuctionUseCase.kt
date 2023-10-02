package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.gui.domain.util.DtoExt.itemStack
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.KyoriExt.sendMessage
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
    private val serializer: Encoder,
    private val stringSerializer: KyoriComponentSerializer
) : RemoveAuctionUseCase {

    override suspend operator fun invoke(input: RemoveAuctionUseCase.Params): Boolean {
        val (_auction, player) = input

        val auction = dataSource.fetchAuction(_auction.id) ?: return false
        val owner = Bukkit.getOfflinePlayer(UUID.fromString(auction.minecraftUuid))
        if (owner.uniqueId != player.uniqueId) {
            stringSerializer.sendMessage(translation.auction.notAuctionOwner, player)
            return false
        }
        val item = auction.itemStack(serializer)
        if (player.inventory.firstEmpty() == -1) {
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.auction.inventoryFull, player)
            return false
        }
        val result = dataSource.deleteAuction(auction)
        return if (result != null) {
            stringSerializer.sendMessage(translation.auction.auctionDeleted, player)
            player.inventory.addItem(item)
            true
        } else {
            stringSerializer.sendMessage(translation.general.unexpectedError, player)
            false
        }
    }
}
