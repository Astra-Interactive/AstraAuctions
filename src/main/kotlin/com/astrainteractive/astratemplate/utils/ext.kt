package com.astrainteractive.astratemplate.utils

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astratemplate.api.AuctionSort
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.utils.encoding.BukkitInputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import java.util.*

val AuctionDTO.itemStack: ItemStack
    get() = Serializer.fromByteArray<ItemStack>(item, BukkitInputStreamProvider)
val AuctionDTO.owner: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUuid))

/**
 * @param sortType type of sort [AuctionSort]
 * @return result - list of sorted auctions. If [result] is null, [_currentAuctions] will be taken
 */
fun List<AuctionDTO>.sortBy(sortType: AuctionSort): List<AuctionDTO> {
    return when (sortType) {
        AuctionSort.MATERIAL_DESC -> sortedByDescending { it.itemStack.type }
        AuctionSort.MATERIAL_ASC -> sortedBy { it.itemStack.type }

        AuctionSort.DATE_ASC -> sortedBy { it.time }
        AuctionSort.DATE_DESC -> sortedByDescending { it.time }

        AuctionSort.NAME_ASC -> sortedBy { it.itemStack.itemMeta?.displayName }
        AuctionSort.NAME_DESC -> sortedByDescending { it.itemStack.itemMeta?.displayName }

        AuctionSort.PRICE_ASC -> sortedBy { it.price }
        AuctionSort.PRICE_DESC -> sortedByDescending { it.price }


        AuctionSort.PLAYER_ASC -> sortedBy {
            Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
        }
        AuctionSort.PLAYER_DESC -> sortedByDescending {
            Bukkit.getOfflinePlayer(UUID.fromString(it.minecraftUuid)).name ?: ""
        }
        else -> this
    }
}