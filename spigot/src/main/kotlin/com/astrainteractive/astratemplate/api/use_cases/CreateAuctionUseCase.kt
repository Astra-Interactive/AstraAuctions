package com.astrainteractive.astratemplate.api.use_cases

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.DataSourceModule
import com.astrainteractive.astratemplate.modules.TranslationModule
import com.astrainteractive.astratemplate.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.domain.IUseCase
import ru.astrainteractive.astralibs.utils.encoding.BukkitOutputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import ru.astrainteractive.astralibs.utils.uuid
import kotlin.math.max
import kotlin.math.min

class CreateAuctionUseCase : IUseCase<Boolean, CreateAuctionUseCase.Params> {
    private val dataSource by DataSourceModule
    private val translation by TranslationModule
    private val config by ConfigModule

    class Params(
        val maxAuctionsAllowed: Int,
        val player: Player,
        val price: Float,
        val amount: Int,
        val item: ItemStack?
    )

    override suspend fun run(params: CreateAuctionUseCase.Params): Boolean {
        val player = params.player
        val maxAuctionsAllowed = params.maxAuctionsAllowed
        val price = params.price
        val item = params.item
        var amount = params.amount

        if (item == null || item.type == Material.AIR) {
            player.sendMessage(translation.wrongItemInHand)
            player.playSound(config.sounds.fail)
            return false
        }

        amount = min(item.amount, amount)
        amount = max(amount, 1)

        val auctionsAmount = dataSource.countPlayerAuctions(player.uuid) ?: 0
        if (auctionsAmount > maxAuctionsAllowed) {
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
            Serializer.toByteArray(itemClone, BukkitOutputStreamProvider),
            price,
            expired = false
        )

        val result = dataSource.insertAuction(auction)
        if (result != null) {
            item.amount -= amount
            player.sendMessage(translation.auctionAdded)
            player.playSound(config.sounds.success)
            if (config.auction.announce)
                Bukkit.broadcastMessage(translation.broadcast.replace("%player%", player.name))
            return true
        } else {
            player.playSound(config.sounds.fail)
            player.sendMessage(translation.dbError)
            return false
        }


    }
}