package com.astrainteractive.astramarket.domain.entities

import ru.astrainteractive.astralibs.orm.database.Column
import ru.astrainteractive.astralibs.orm.database.Constructable
import ru.astrainteractive.astralibs.orm.database.Entity
import ru.astrainteractive.astralibs.orm.database.Table


object AuctionTable : Table<Long>("auctions") {
    override val id: Column<Long> = long("id").primaryKey().autoIncrement()
    val discordId = text("discord_id").nullable()
    val minecraftUuid = text("minecraft_uuid")
    val time = long("time")
    val item = byteArray("item")
    val price = float("price")
    val expired = bool("expired")
}


class Auction : Entity<Long>(AuctionTable) {
    val id by AuctionTable.id
    val discordId by AuctionTable.discordId
    val minecraftUuid by AuctionTable.minecraftUuid
    val time by AuctionTable.time
    val item by AuctionTable.item
    val price by AuctionTable.price
    var expired by AuctionTable.expired
    companion object: Constructable<Auction>(::Auction)
}