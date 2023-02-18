package com.astrainteractive.astramarket.utils

import com.astrainteractive.astramarket.domain.dto.AuctionDTO
import com.astrainteractive.astramarket.api.AuctionSort
import com.astrainteractive.astramarket.modules.Modules
import com.astrainteractive.astramarket.plugin.AuctionConfig
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.DBSyntax
import java.util.*

val AuctionDTO.itemStack: ItemStack
    get() = Modules.bukkitSerializer.value.fromByteArray<ItemStack>(item)
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


fun AuctionConfig.Connection.toDBConnection(): Pair<DBConnection, DBSyntax> {
    val sqliteConnection = DBConnection.SQLite("dbv2_auction.db") to DBSyntax.SQLite
    if (this.sqlite) return sqliteConnection
    val mySqlConnection = this.mysql?.let {
        DBConnection.MySQL(
            database = it.database,
            ip = it.ip,
            port = it.port,
            username = it.username,
            password = it.password,
            sessionVariables = it.sessionVariables.toTypedArray()
        ) to DBSyntax.MySQL
    }

    return mySqlConnection ?: sqliteConnection
}