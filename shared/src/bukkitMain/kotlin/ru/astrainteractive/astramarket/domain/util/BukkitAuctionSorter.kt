package ru.astrainteractive.astramarket.domain.util

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.domain.model.AuctionSort
import java.util.UUID

class BukkitAuctionSorter(private val encoder: Encoder) : AuctionSorter {
    private fun AuctionDTO.itemStack(serializer: Encoder): ItemStack {
        return serializer.fromByteArray<ItemStack>(
            item
        )
    }

    override fun sort(sortType: AuctionSort, list: List<AuctionDTO>): List<AuctionDTO> {
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
        }
    }
}
