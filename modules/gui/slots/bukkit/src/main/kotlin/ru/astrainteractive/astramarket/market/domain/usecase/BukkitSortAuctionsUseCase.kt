package ru.astrainteractive.astramarket.market.domain.usecase

import org.bukkit.Bukkit
import ru.astrainteractive.astramarket.core.itemstack.ItemStackEncoder
import ru.astrainteractive.astramarket.core.util.sortedBy
import ru.astrainteractive.astramarket.market.domain.model.AuctionSort
import java.util.UUID

internal class BukkitSortAuctionsUseCase(private val itemStackEncoder: ItemStackEncoder) : SortAuctionsUseCase {

    override fun invoke(input: SortAuctionsUseCase.Input): SortAuctionsUseCase.Output {
        val sortType = input.sortType
        val list = input.list
        return when (sortType) {
            is AuctionSort.Material -> list.sortedBy(
                isAsc = sortType.isAsc,
                selector = {
                    itemStackEncoder
                        .toItemStack(it.item)
                        .getOrNull()
                        ?.type
                }
            )

            is AuctionSort.Date -> list.sortedBy(
                isAsc = sortType.isAsc,
                selector = { it.time }
            )

            is AuctionSort.Name -> list.sortedBy(
                isAsc = sortType.isAsc,
                selector = {
                    itemStackEncoder.toItemStack(it.item)
                        .getOrNull()
                        ?.itemMeta
                        ?.displayName
                }
            )

            is AuctionSort.Price -> list.sortedBy(
                isAsc = sortType.isAsc,
                selector = { it.price }
            )

            is AuctionSort.Player -> list.sortedBy(
                isAsc = sortType.isAsc,
                selector = {
                    Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
                }
            )
        }.let(SortAuctionsUseCase::Output)
    }
}
