package ru.astrainteractive.astramarket.data.dao.market.util

import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import java.util.UUID

suspend fun MarketRepository.findPlayersWithSlots(isExpired: Boolean): List<PlayerAndSlots> {
    return getSlots(isExpired)
        .orEmpty()
        .groupBy(MarketSlot::minecraftUuid)
        .map { (uuid, slots) ->
            PlayerAndSlots(
                minecraftUUID = UUID.fromString(uuid),
                slots = slots,
                minecraftUsername = slots.firstOrNull()
                    ?.minecraftUsername
                    .orEmpty()
            )
        }
}
