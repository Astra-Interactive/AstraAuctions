package ru.astrainteractive.astramarket.data.dao.market.util

import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import java.util.UUID

suspend fun MarketRepository.findPlayersWithSlots(isExpired: Boolean): Result<List<PlayerAndSlots>> {
    return getSlots(isExpired).map { slots ->
        slots
            .groupBy(MarketSlot::minecraftUuid)
            .map { (uuid, playerSlots) ->
                PlayerAndSlots(
                    minecraftUUID = UUID.fromString(uuid),
                    slots = playerSlots,
                    minecraftUsername = playerSlots.first().minecraftUsername
                )
            }
    }
}
