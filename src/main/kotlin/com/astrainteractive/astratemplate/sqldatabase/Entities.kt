package com.astrainteractive.astratemplate.sqldatabase

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.database.ColumnInfo
import ru.astrainteractive.astralibs.database.Entity
import ru.astrainteractive.astralibs.database.PrimaryKey
import ru.astrainteractive.astralibs.utils.encoding.BukkitInputStreamProvider
import ru.astrainteractive.astralibs.utils.encoding.Serializer
import java.util.*

@Entity(Auction.TABLE)
data class Auction(
    @ColumnInfo("id")
    @PrimaryKey
    val id: Long,
    @ColumnInfo("discord_id")
    val discordId: String?,
    @ColumnInfo("minecraft_uuid")
    val minecraftUuid: String,
    @ColumnInfo("time")
    val time: Long = System.currentTimeMillis(),
    @ColumnInfo("item")
    val item: ByteArray,
    @ColumnInfo("price")
    val price: Float,
    @ColumnInfo("expired")
    var expired: Boolean = false,
) {
    val itemStack: ItemStack
        get() = Serializer.fromByteArray<ItemStack>(item,BukkitInputStreamProvider)
    val owner: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUuid))


    companion object {
        const val TABLE = "auctions"
    }
}