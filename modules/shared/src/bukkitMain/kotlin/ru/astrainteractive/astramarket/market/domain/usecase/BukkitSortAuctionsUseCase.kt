package ru.astrainteractive.astramarket.market.domain.usecase

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.encoder.ObjectEncoder
import ru.astrainteractive.astramarket.api.market.model.MarketSlot
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import java.util.UUID

internal class BukkitSortAuctionsUseCase(private val encoder: ObjectEncoder) : SortAuctionsUseCase {
    private fun MarketSlot.itemStack(serializer: ObjectEncoder): ItemStack {
        return serializer.fromByteArray(
            item
        )
    }

    override fun invoke(input: SortAuctionsUseCase.Input): SortAuctionsUseCase.Output {
        val sortType = input.sortType
        val list = input.list
        return when (sortType) {
            AuctionSort.MATERIAL_DESC -> list.sortedByDescending { it.itemStack(encoder).type }
            AuctionSort.MATERIAL_ASC -> list.sortedBy { it.itemStack(encoder).type }

            AuctionSort.DATE_ASC -> list.sortedBy { it.time }
            AuctionSort.DATE_DESC -> list.sortedByDescending { it.time }

            AuctionSort.NAME_ASC -> list.sortedBy { it.itemStack(encoder).itemMeta?.displayName }
            AuctionSort.NAME_DESC -> list.sortedByDescending { it.itemStack(encoder).itemMeta?.displayName }

            AuctionSort.PRICE_ASC -> list.sortedBy { it.price }
            AuctionSort.PRICE_DESC -> list.sortedByDescending { it.price }

            AuctionSort.PLAYER_ASC -> list.sortedBy {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }

            AuctionSort.PLAYER_DESC -> list.sortedByDescending {
                Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
            }

            else -> list
        }.let(SortAuctionsUseCase::Output)
    }
}
