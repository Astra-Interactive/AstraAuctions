@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.data.dao.market.util

import kotlinx.coroutines.test.runTest
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astramarket.data.dao.market.fake.FakeMarketRepository
import ru.astrainteractive.astramarket.data.dao.market.model.MarketSlot
import ru.astrainteractive.astramarket.data.dao.market.model.MarketStorageError
import ru.astrainteractive.astramarket.data.dao.market.model.PlayerAndSlots
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

class MarketRepositoryExtTest {
    private val firstPlayer = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    private val secondPlayer = UUID.fromString("550e8400-e29b-41d4-a716-446655440002")

    private fun slot(id: Int, owner: UUID, username: String, expired: Boolean): MarketSlot {
        return MarketSlot(
            id = id,
            minecraftUuid = owner.toString(),
            minecraftUsername = username,
            time = Instant.fromEpochMilliseconds(0),
            item = EncodedObject.ByteArray(byteArrayOf()),
            price = 10f,
            expired = expired
        )
    }

    @Test
    fun GIVEN_slots_of_two_players_WHEN_players_with_slots_are_found_THEN_slots_are_grouped_by_owner() {
        runTest {
            val repository = FakeMarketRepository(
                slots = listOf(
                    slot(
                        id = 1,
                        owner = firstPlayer,
                        username = "First",
                        expired = false
                    ),
                    slot(
                        id = 2,
                        owner = firstPlayer,
                        username = "First",
                        expired = false
                    ),
                    slot(
                        id = 3,
                        owner = secondPlayer,
                        username = "Second",
                        expired = false
                    )
                )
            )

            val players = repository.findPlayersWithSlots(isExpired = false).getOrThrow()

            val byUuid = players.associateBy(PlayerAndSlots::minecraftUUID)
            assertEquals(2, byUuid.getValue(firstPlayer).slots.size)
            assertEquals("First", byUuid.getValue(firstPlayer).minecraftUsername)
            assertEquals(1, byUuid.getValue(secondPlayer).slots.size)
            assertEquals("Second", byUuid.getValue(secondPlayer).minecraftUsername)
        }
    }

    @Test
    fun GIVEN_only_expired_slots_WHEN_active_players_are_found_THEN_returns_no_players() {
        runTest {
            val repository = FakeMarketRepository(
                slots = listOf(
                    slot(
                        id = 1,
                        owner = firstPlayer,
                        username = "First",
                        expired = true
                    )
                )
            )

            val players = repository.findPlayersWithSlots(isExpired = false).getOrThrow()

            assertTrue(players.isEmpty())
        }
    }

    @Test
    fun GIVEN_storage_failure_WHEN_players_with_slots_are_found_THEN_returns_the_failure() {
        runTest {
            val repository = FakeMarketRepository(slots = emptyList())
            repository.failingOperations += FakeMarketRepository.Operation.READ

            val result = repository.findPlayersWithSlots(isExpired = false)

            assertIs<MarketStorageError.QueryFailed>(result.exceptionOrNull())
        }
    }
}
