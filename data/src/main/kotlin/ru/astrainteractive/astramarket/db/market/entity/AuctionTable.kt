package ru.astrainteractive.astramarket.db.market.entity

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table

internal object AuctionTable : Table<Int>("auctions") {
    override val id: Column<Int> = integer("id").primaryKey().autoIncrement()
    val discordId = text("discord_id").nullable()
    val minecraftUuid = text("minecraft_uuid")
    val time = bigint("time")
    val item = byteArray("item", 6132)
    val price = float("price")
    val expired = bool("expired")
}

internal class Auction : Entity<Int>(AuctionTable) {
    val id by AuctionTable.id
    val discordId by AuctionTable.discordId
    val minecraftUuid by AuctionTable.minecraftUuid
    val time by AuctionTable.time
    val item by AuctionTable.item
    val price by AuctionTable.price
    var expired by AuctionTable.expired
    companion object : Constructable<Auction>(::Auction)
}
