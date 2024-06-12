package ru.astrainteractive.astramarket.market.domain.usecase

import org.bukkit.Bukkit
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import java.util.UUID

internal class BukkitSortAuctionsUseCase(private val itemStackEncoder: ItemStackEncoder) : SortAuctionsUseCase {

    override fun invoke(input: SortAuctionsUseCase.Input): SortAuctionsUseCase.Output {
        val sortType = input.sortType
        val list = input.list
        return when (sortType) {
            AuctionSort.MATERIAL_DESC -> list.sortedByDescending { itemStackEncoder.toItemStack(it.item).type }
            AuctionSort.MATERIAL_ASC -> list.sortedBy { itemStackEncoder.toItemStack(it.item).type }

            AuctionSort.DATE_ASC -> list.sortedBy { it.time }
            AuctionSort.DATE_DESC -> list.sortedByDescending { it.time }

            AuctionSort.NAME_ASC -> list.sortedBy { itemStackEncoder.toItemStack(it.item).itemMeta?.displayName }
            AuctionSort.NAME_DESC -> list.sortedByDescending {
                itemStackEncoder.toItemStack(
                    it.item
                ).itemMeta?.displayName
            }

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
