package ru.astrainteractive.astramarket.db.market.entity

import org.jetbrains.exposed.dao.id.IntIdTable

 object AuctionTableV2 : IntIdTable("auctions_v2") {
    val minecraftUuid = text("minecraft_uuid")
    val minecraftUsername = text("minecraft_username").default("")
    val time = long("time")
    val item = text("item")
    val price = float("price")
    val expired = bool("expired")
}
