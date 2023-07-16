package com.astrainteractive.astramarket.gui.domain.usecases

import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.util.itemStack
import com.astrainteractive.astramarket.util.playSound
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.klibs.kdi.getValue
import java.util.*

/**
 * @param _auction auction to remove
 * @param player owner of auction
 * @return boolean - true if succesfully removed
 */
class RemoveAuctionUseCase(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val serializer: Serializer
) : UseCase<Boolean, RemoveAuctionUseCase.Params> {
    class Params(
        val auction: AuctionDTO,
        val player: Player
    ) {
        operator fun component1() = auction
        operator fun component2() = player
    }

    override suspend fun run(params: Params): Boolean {
        val (_auction, player) = params

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
