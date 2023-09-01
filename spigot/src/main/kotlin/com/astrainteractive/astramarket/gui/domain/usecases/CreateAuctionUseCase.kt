package com.astrainteractive.astramarket.gui.domain.usecases

import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import com.astrainteractive.astramarket.util.playSound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.util.uuid
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import kotlin.math.max
import kotlin.math.min

class CreateAuctionUseCase(
    private val dataSource: AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val serializer: Serializer
) : UseCase.Parametrized<CreateAuctionUseCase.Params, Boolean> {
    class Params(
        val maxAuctionsAllowed: Int,
        val player: Player,
        val price: Float,
        val amount: Int,
        val item: ItemStack?
    )

    override suspend operator fun invoke(input: Params): Boolean {
        val player = input.player
        val maxAuctionsAllowed = input.maxAuctionsAllowed
        val price = input.price
        val item = input.item
        var amount = input.amount

        if (item == null || item.type == Material.AIR) {
            player.sendMessage(translation.wrongItemInHand)
            player.playSound(config.sounds.fail)
            return false
        }

        amount = min(item.amount, amount)
        amount = max(amount, 1)

        val auctionsAmount = dataSource.countPlayerAuctions(player.uuid) ?: 0
        if (auctionsAmount >= maxAuctionsAllowed) {
            player.sendMessage(translation.maxAuctions)
            player.playSound(config.sounds.fail)
            return false
        }
        if (price > config.auction.maxPrice || price < config.auction.minPrice) {
            player.sendMessage(translation.wrongPrice)
            player.playSound(config.sounds.fail)
            return false
        }

        val itemClone = item.clone().apply { this.amount = amount }
        val auction = AuctionDTO(
            -1,
            "",
            player.uuid,
            System.currentTimeMillis(),
            serializer.toByteArray(itemClone),
            price,
            expired = false
        )

        val result = dataSource.insertAuction(auction)
        if (result != null) {
            item.amount -= amount
            player.sendMessage(translation.auctionAdded)
            player.playSound(config.sounds.success)
            if (config.auction.announce) {
                Bukkit.broadcastMessage(translation.broadcast.replace("%player%", player.name))
            }
            return true
        } else {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.dbError)
            return false
        }
    }
}
