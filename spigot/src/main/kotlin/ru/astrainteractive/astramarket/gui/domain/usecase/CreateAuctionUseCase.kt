package ru.astrainteractive.astramarket.gui.domain.usecase

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.util.uuid
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.util.KyoriExt.sendMessage
import ru.astrainteractive.astramarket.util.playSound
import ru.astrainteractive.klibs.mikro.core.domain.UseCase
import kotlin.math.max
import kotlin.math.min

interface CreateAuctionUseCase : UseCase.Parametrized<CreateAuctionUseCase.Params, Boolean> {
    class Params(
        val maxAuctionsAllowed: Int,
        val player: Player,
        val price: Float,
        val amount: Int,
        val item: ItemStack?
    )
}

internal class CreateAuctionUseCaseImpl(
    private val dataSource: ru.astrainteractive.astramarket.api.market.AuctionsAPI,
    private val translation: Translation,
    private val config: AuctionConfig,
    private val serializer: Encoder,
    private val stringSerializer: KyoriComponentSerializer
) : CreateAuctionUseCase {

    override suspend operator fun invoke(input: CreateAuctionUseCase.Params): Boolean {
        val player = input.player
        val maxAuctionsAllowed = input.maxAuctionsAllowed
        val price = input.price
        val item = input.item
        var amount = input.amount

        if (item == null || item.type == Material.AIR) {
            stringSerializer.sendMessage(translation.auction.wrongItemInHand, player)
            player.playSound(config.sounds.fail)
            return false
        }

        amount = min(item.amount, amount)
        amount = max(amount, 1)

        val auctionsAmount = dataSource.countPlayerAuctions(player.uuid) ?: 0
        if (auctionsAmount >= maxAuctionsAllowed) {
            stringSerializer.sendMessage(translation.auction.maxAuctions, player)
            player.playSound(config.sounds.fail)
            return false
        }
        if (price > config.auction.maxPrice || price < config.auction.minPrice) {
            stringSerializer.sendMessage(translation.auction.wrongPrice, player)
            player.playSound(config.sounds.fail)
            return false
        }

        val itemClone = item.clone().apply { this.amount = amount }
        val auction = ru.astrainteractive.astramarket.api.market.dto.AuctionDTO(
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
            stringSerializer.sendMessage(translation.auction.auctionAdded, player)
            player.playSound(config.sounds.success)
            if (config.auction.announce) {
                Bukkit.broadcast(
                    stringSerializer.toComponent(translation.auction.broadcast.replace("%player%", player.name))
                )
            }
            return true
        } else {
            player.playSound(config.sounds.fail)
            stringSerializer.sendMessage(translation.general.dbError, player)
            return false
        }
    }
}
