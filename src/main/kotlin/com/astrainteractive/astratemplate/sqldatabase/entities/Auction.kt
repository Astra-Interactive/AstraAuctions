package com.astrainteractive.astratemplate.sqldatabase.entities


import com.astrainteractive.astralibs.catching
import com.astrainteractive.astratemplate.utils.NMSHelper
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.util.*
import kotlin.math.exp

data class Auction(
    val id: Long,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long = System.currentTimeMillis(),
    val item: ByteArray,
    val price: Float,
    var expired: Boolean = false
) {
    constructor(player: Player, itemStack: ItemStack, price: Float) : this(
        -1L,
        null,
        player.uniqueId.toString(),
        System.currentTimeMillis(),
        NMSHelper.serializeItem(itemStack),
        price
    )

    val itemStack: ItemStack
        get() = NMSHelper.deserializeItem(item,time)

    fun uuidToName(uuid: String) = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).name ?: "Player not found"

    val owner: OfflinePlayer
        get() = Bukkit.getOfflinePlayer(UUID.fromString(minecraftUuid))

    override fun toString() =
        "Auction(id=$id, discordId=$discordId, uuid=$minecraftUuid, userName=${uuidToName(minecraftUuid)} time=$time, price=$price, item=${itemStack})"

    companion object {
        fun fromResultSet(rs: ResultSet?) = catching {
            rs?.let {
                return@catching Auction(
                    id = it.getLong(id.name),
                    discordId = it.getString(discordId.name),
                    minecraftUuid = it.getString(minecraftUuid.name),
                    time = it.getLong(time.name),
                    item = it.getBytes(item.name),
                    price = it.getFloat(price.name),
                    expired = it.getBoolean(expired.name)
                )
            }
        }

        val table: String
            get() = "auctions"
        val discordId: EntityInfo
            get() = EntityInfo("discord_id", "varchar(16)")
        val minecraftUuid: EntityInfo
            get() = EntityInfo("minecraft_uuid", "varchar(16)")
        val time: EntityInfo
            get() = EntityInfo("time", "bigint")
        val item: EntityInfo
            get() = EntityInfo("item", "varbinary")
        val price: EntityInfo
            get() = EntityInfo("price", "FLOAT")
        val expired: EntityInfo
            get() = EntityInfo("expired", "BIT")
        val id: EntityInfo
            get() = EntityInfo("id", "INTEGER")
    }
}

