package ru.astrainteractive.astramarket.db.market.entity

import org.jetbrains.exposed.dao.id.IntIdTable

internal object AuctionTable : IntIdTable("auctions") {
    val minecraftUuid = text("minecraft_uuid")
    val minecraftUsername = text("minecraft_username").default("")
    val time = long("time")
    val item = binary("item", 6132)
    val price = float("price")
    val expired = bool("expired")
}
