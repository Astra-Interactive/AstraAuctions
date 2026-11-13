@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.data.dao.market.util

import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ListExtTest {
    private val firstPlayer = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    private val secondPlayer = UUID.fromString("550e8400-e29b-41d4-a716-446655440002")

    private fun slot(id: Int, owner: UUID, username: String): MarketSlot {
        return MarketSlot(
            id = id,
            minecraftUuid = owner.toString(),
            minecraftUsername = username,
            time = Instant.fromEpochMilliseconds(0),
            item = EncodedObject.ByteArray(byteArrayOf()),
            price = 10f,
            expired = false
        )
    }

    @Test
    fun GIVEN_slots_of_two_players_WHEN_converted_to_players_and_slots_THEN_slots_are_grouped_by_owner() {
        val slots = listOf(
            slot(
                id = 1,
                owner = firstPlayer,
                username = "First"
            ),
            slot(
                id = 2,
                owner = firstPlayer,
                username = "First"
            ),
            slot(
                id = 3,
                owner = secondPlayer,
                username = "Second"
            )
        )

        val players = slots.toPlayersAndSlots()

        val byUuid = players.associateBy(PlayerAndSlots::minecraftUUID)
        assertEquals(2, byUuid.getValue(firstPlayer).slots.size)
        assertEquals("First", byUuid.getValue(firstPlayer).minecraftUsername)
        assertEquals(1, byUuid.getValue(secondPlayer).slots.size)
        assertEquals("Second", byUuid.getValue(secondPlayer).minecraftUsername)
    }
}
