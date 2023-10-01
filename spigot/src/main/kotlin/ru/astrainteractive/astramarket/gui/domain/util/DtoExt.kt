package ru.astrainteractive.astramarket.gui.domain.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astramarket.api.market.dto.AuctionDTO
import ru.astrainteractive.astramarket.gui.domain.model.AuctionSort
import java.util.UUID

object DtoExt {
    fun AuctionDTO.itemStack(serializer: Serializer): ItemStack = serializer.fromByteArray<ItemStack>(
        item
    )
    val AuctionDTO.owner: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUuid))

    /**
     * @param sortType type of sort [AuctionSort]
     * @return result - list of sorted auctions. If [result] is null, [_currentAuctions] will be taken
     */
    fun List<AuctionDTO>.sortBy(
        sortType: AuctionSort,
        serializer: Serializer
    ): List<AuctionDTO> {
        return when (sortType) {
            AuctionSort.MATERIAL_DESC -> sortedByDescending { it.itemStack(serializer).type }
            AuctionSort.MATERIAL_ASC -> sortedBy { it.itemStack(serializer).type }

            AuctionSort.DATE_ASC -> sortedBy { it.time }
            AuctionSort.DATE_DESC -> sortedByDescending { it.time }

            AuctionSort.NAME_ASC -> sortedBy { it.itemStack(serializer).itemMeta?.displayName }
            AuctionSort.NAME_DESC -> sortedByDescending { it.itemStack(serializer).itemMeta?.displayName }

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
}
