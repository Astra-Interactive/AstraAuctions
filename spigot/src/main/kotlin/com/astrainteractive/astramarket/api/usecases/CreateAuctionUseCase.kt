package com.astrainteractive.astramarket.api.usecases

import com.astrainteractive.astramarket.di.impl.RootModuleImpl
import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.utils.playSound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.domain.UseCase
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.utils.uuid
import kotlin.math.max
import kotlin.math.min

class CreateAuctionUseCase : UseCase<Boolean, CreateAuctionUseCase.Params> {
    private val dataSource by RootModuleImpl.auctionsApi
    private val translation by RootModuleImpl.translation
    private val config by RootModuleImpl.configuration
    private val serializer by RootModuleImpl.bukkitSerializer

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
