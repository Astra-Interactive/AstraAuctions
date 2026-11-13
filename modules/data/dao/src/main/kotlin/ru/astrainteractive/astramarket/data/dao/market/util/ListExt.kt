package ru.astrainteractive.astramarket.data.dao.market.util

import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import java.util.UUID

fun List<MarketSlot>.toPlayersAndSlots(): List<PlayerAndSlots> {
    return groupBy(MarketSlot::minecraftUuid)
        .map { (uuid, playerSlots) ->
            PlayerAndSlots(
                minecraftUUID = UUID.fromString(uuid),
                slots = playerSlots,
                minecraftUsername = playerSlots.first().minecraftUsername
            )
        }
}
