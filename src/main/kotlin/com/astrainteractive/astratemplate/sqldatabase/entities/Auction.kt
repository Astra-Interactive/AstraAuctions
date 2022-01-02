package com.astrainteractive.astratemplate.sqldatabase.entities


import com.astrainteractive.astralibs.catching
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet

data class Auction(
    val id: Long,
    val discordId: String?,
    val minecraftUuid: String,
    val time: Long = System.currentTimeMillis(),
    val item: ByteArray,
    val price: Float
) {
    constructor(player: Player,itemStack: ItemStack,price: Float):this(-1L,null,player.uniqueId.toString(),System.currentTimeMillis(),itemStack.serializeAsBytes(),price)
    val itemStack: ItemStack
        get() = ItemStack.deserializeBytes(item)

    companion object {
        fun fromResultSet(rs: ResultSet?) = catching {
            rs?.let {
                return@catching Auction(
                    id = it.getLong(id.name),
                    discordId = it.getString(discordId.name),
                    minecraftUuid = it.getString(minecraftUuid.name),
                    time = it.getLong(time.name),
                    item = it.getBytes(item.name),
                    price = it.getFloat(price.name)
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
        val id: EntityInfo
            get() = EntityInfo("id", "INTEGER")
    }
}

data class EntityInfo(val name: String, val type: String)

