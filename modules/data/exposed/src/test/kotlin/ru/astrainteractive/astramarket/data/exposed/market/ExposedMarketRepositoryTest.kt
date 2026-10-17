@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.data.exposed.market

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.Database
import ru.astrainteractive.astramarket.data.dao.market.model.MarketStorageError
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class ExposedMarketRepositoryTest {
    private suspend fun withTestContext(block: suspend TestContext.() -> Unit) {
        val ctx = TestContext()
        ctx.onStart()
        try {
            ctx.block()
        } finally {
            ctx.onDisable()
        }
    }

    @Test
    fun GIVEN_empty_database_WHEN_insert_slot_THEN_returns_its_id() = runTest {
        withTestContext {
            val insertedId = marketRepository.insertSlot(createMarketSlot())

            assertTrue(insertedId.isSuccess)
        }
    }

    @Test
    fun GIVEN_inserted_slot_WHEN_get_slot_by_id_THEN_returns_slot_with_matching_data() = runTest {
        withTestContext {
            val slot = createMarketSlot(price = 250.0f, username = "Trader")
            val insertedId = marketRepository.insertSlot(slot).getOrThrow()

            val retrieved = marketRepository.getSlot(insertedId).getOrThrow()

            assertNotNull(retrieved)
            assertEquals(slot.price, retrieved.price)
            assertEquals(slot.minecraftUuid, retrieved.minecraftUuid)
            assertEquals(slot.minecraftUsername, retrieved.minecraftUsername)
        }
    }

    @Test
    fun GIVEN_non_existent_id_WHEN_get_slot_THEN_returns_no_slot() = runTest {
        withTestContext {
            val retrieved = marketRepository.getSlot(99999).getOrThrow()

            assertNull(retrieved)
        }
    }

    @Test
    fun GIVEN_inserted_active_slot_WHEN_get_active_slots_THEN_slot_appears_in_list() = runTest {
        withTestContext {
            val slot = createMarketSlot(expired = false)
            marketRepository.insertSlot(slot)

            val activeSlots = marketRepository.getSlots(isExpired = false).getOrThrow()

            assertEquals(1, activeSlots.size)
            assertEquals(slot.minecraftUuid, activeSlots.first().minecraftUuid)
        }
    }

    @Test
    fun GIVEN_inserted_active_slot_WHEN_get_expired_slots_THEN_returns_empty_list() = runTest {
        withTestContext {
            marketRepository.insertSlot(createMarketSlot(expired = false))

            val expiredSlots = marketRepository.getSlots(isExpired = true).getOrThrow()

            assertTrue(expiredSlots.isEmpty())
        }
    }

    @Test
    fun GIVEN_inserted_active_slot_WHEN_expire_slot_THEN_slot_moves_to_expired_list() = runTest {
        withTestContext {
            val insertedId = marketRepository.insertSlot(createMarketSlot(expired = false)).getOrThrow()
            val activeSlot = assertNotNull(marketRepository.getSlot(insertedId).getOrThrow())

            marketRepository.expireSlot(activeSlot).getOrThrow()

            assertEquals(1, marketRepository.getSlots(isExpired = true).getOrThrow().size)
            assertTrue(marketRepository.getSlots(isExpired = false).getOrThrow().isEmpty())
        }
    }

    @Test
    fun GIVEN_inserted_slot_WHEN_delete_slot_THEN_slot_cannot_be_retrieved() = runTest {
        withTestContext {
            val insertedId = marketRepository.insertSlot(createMarketSlot()).getOrThrow()
            val insertedSlot = assertNotNull(marketRepository.getSlot(insertedId).getOrThrow())

            marketRepository.deleteSlot(insertedSlot).getOrThrow()

            assertNull(marketRepository.getSlot(insertedId).getOrThrow())
            assertTrue(marketRepository.getSlots(isExpired = false).getOrThrow().none { slot -> slot.id == insertedId })
        }
    }

    @Test
    fun GIVEN_multiple_slots_for_player_WHEN_count_player_slots_THEN_returns_exact_count() = runTest {
        withTestContext {
            val uuid = "550e8400-e29b-41d4-a716-446655440001"
            repeat(3) { marketRepository.insertSlot(createMarketSlot(uuid = uuid)) }

            val count = marketRepository.countPlayerSlots(uuid).getOrThrow()

            assertEquals(3, count)
        }
    }

    @Test
    fun GIVEN_no_slots_for_player_WHEN_count_player_slots_THEN_returns_zero() = runTest {
        withTestContext {
            val count = marketRepository.countPlayerSlots("550e8400-e29b-41d4-a716-446655440001").getOrThrow()

            assertEquals(0, count)
        }
    }

    @Test
    fun GIVEN_slots_for_two_players_WHEN_get_user_slots_THEN_returns_only_requested_player_slots() = runTest {
        withTestContext {
            val firstPlayerUuid = "550e8400-e29b-41d4-a716-446655440001"
            val secondPlayerUuid = "550e8400-e29b-41d4-a716-446655440002"
            repeat(2) { marketRepository.insertSlot(createMarketSlot(uuid = firstPlayerUuid)) }
            marketRepository.insertSlot(createMarketSlot(uuid = secondPlayerUuid))

            val firstPlayerSlots = marketRepository.getUserSlots(firstPlayerUuid, isExpired = false).getOrThrow()

            assertEquals(2, firstPlayerSlots.size)
            assertTrue(firstPlayerSlots.all { slot -> slot.minecraftUuid == firstPlayerUuid })
        }
    }

    @Test
    fun GIVEN_active_and_expired_slots_for_player_WHEN_get_user_active_slots_THEN_returns_only_active_slots() = runTest {
        withTestContext {
            val uuid = "550e8400-e29b-41d4-a716-446655440001"
            marketRepository.insertSlot(createMarketSlot(uuid = uuid, expired = false))
            marketRepository.insertSlot(createMarketSlot(uuid = uuid, expired = true))

            val activeUserSlots = marketRepository.getUserSlots(uuid, isExpired = false).getOrThrow()

            assertEquals(1, activeUserSlots.size)
            assertTrue(activeUserSlots.all { slot -> !slot.expired })
        }
    }

    @Test
    fun GIVEN_old_and_new_slots_WHEN_get_slots_created_before_THEN_returns_only_old_slots() = runTest {
        withTestContext {
            val oneHour = 1.hours
            marketRepository.insertSlot(createMarketSlot(timeOffset = -oneHour * 2))
            marketRepository.insertSlot(createMarketSlot(timeOffset = -oneHour * 3))
            marketRepository.insertSlot(createMarketSlot(timeOffset = Duration.ZERO))

            val oldSlots = marketRepository.getSlotsCreatedBefore(Clock.System.now() - oneHour).getOrThrow()

            assertEquals(2, oldSlots.size)
        }
    }

    @Test
    fun GIVEN_unreachable_database_WHEN_get_slots_THEN_returns_query_failed() = runTest {
        val repository = ExposedMarketRepository(
            databaseFlow = flow<Database> { error("database is down") },
            dispatchers = DefaultKotlinDispatchers
        )

        val result = repository.getSlots(isExpired = false)

        assertIs<MarketStorageError.QueryFailed>(result.exceptionOrNull())
    }
}
