package com.astrainteractive.astratemplate.sqldatabase

import com.astrainteractive.astralibs.database.ColumnInfo
import com.astrainteractive.astralibs.database.Entity
import com.astrainteractive.astralibs.database.PrimaryKey
import com.astrainteractive.astralibs.utils.ReflectionUtil
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
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
        get() = ReflectionUtil.deserializeItem(item,time)
    val owner: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUuid))


    companion object {
        const val TABLE = "auctions"
    }
}