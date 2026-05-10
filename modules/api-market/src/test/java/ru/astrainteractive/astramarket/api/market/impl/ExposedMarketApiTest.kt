package ru.astrainteractive.astramarket.api.market.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExposedMarketApiTest {
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
    fun GIVEN_emptyDatabase_WHEN_insertSlot_THEN_returnsNonNullId() {
        runTest {
            withTestContext {
                val insertedId = marketApi.insertSlot(createMarketSlot())
                assertNotNull(insertedId)
            }
        }
    }

    @Test
    fun GIVEN_insertedSlot_WHEN_getSlotById_THEN_returnsSlotWithMatchingData() {
        runTest {
            withTestContext {
                val slot = createMarketSlot(price = 250.0f, username = "Trader")
                val insertedId = marketApi.insertSlot(slot)
                assertNotNull(insertedId)

                val retrieved = marketApi.getSlot(insertedId)
                assertNotNull(retrieved)

                assertEquals(slot.price, retrieved.price)
                assertEquals(slot.minecraftUuid, retrieved.minecraftUuid)
                assertEquals(slot.minecraftUsername, retrieved.minecraftUsername)
            }
        }
    }

    @Test
    fun GIVEN_nonExistentId_WHEN_getSlot_THEN_returnsNull() {
        runTest {
            withTestContext {
                val retrieved = marketApi.getSlot(99999)
                assertNull(retrieved)
            }
        }
    }

    @Test
    fun GIVEN_insertedActiveSlot_WHEN_getActiveSlots_THEN_slotAppearsInList() {
        runTest {
            withTestContext {
                val slot = createMarketSlot(expired = false)
                marketApi.insertSlot(slot)

                val activeSlots = marketApi.getSlots(isExpired = false)
                assertNotNull(activeSlots)

                assertEquals(1, activeSlots.size)
                assertEquals(slot.minecraftUuid, activeSlots.first().minecraftUuid)
            }
        }
    }

    @Test
    fun GIVEN_insertedActiveSlot_WHEN_getExpiredSlots_THEN_returnsEmptyList() {
        runTest {
            withTestContext {
                marketApi.insertSlot(createMarketSlot(expired = false))

                val expiredSlots = marketApi.getSlots(isExpired = true)
                assertNotNull(expiredSlots)

                assertTrue(expiredSlots.isEmpty())
            }
        }
    }

    @Test
    fun GIVEN_insertedActiveSlot_WHEN_expireSlot_THEN_slotMovesToExpiredList() {
        runTest {
            withTestContext {
                val insertedId = marketApi.insertSlot(createMarketSlot(expired = false))
                assertNotNull(insertedId)
                val activeSlot = marketApi.getSlot(insertedId)
                assertNotNull(activeSlot)

                marketApi.expireSlot(activeSlot)

                val expiredSlots = marketApi.getSlots(isExpired = true)
                assertNotNull(expiredSlots)
                val activeSlots = marketApi.getSlots(isExpired = false)
                assertNotNull(activeSlots)
                assertEquals(1, expiredSlots.size)
                assertTrue(activeSlots.isEmpty())
            }
        }
    }

    @Test
    fun GIVEN_insertedSlot_WHEN_deleteSlot_THEN_slotCannotBeRetrieved() {
        runTest {
            withTestContext {
                val insertedId = marketApi.insertSlot(createMarketSlot())
                assertNotNull(insertedId)
                val insertedSlot = marketApi.getSlot(insertedId)
                assertNotNull(insertedSlot)

                marketApi.deleteSlot(insertedSlot)

                val retrieved = marketApi.getSlot(insertedId)
                assertNull(retrieved)
            }
        }
    }

    @Test
    fun GIVEN_deletedSlot_WHEN_getSlots_THEN_deletedSlotIsAbsent() {
        runTest {
            withTestContext {
                val insertedId = marketApi.insertSlot(createMarketSlot())
                assertNotNull(insertedId)
                val insertedSlot = marketApi.getSlot(insertedId)
                assertNotNull(insertedSlot)

                marketApi.deleteSlot(insertedSlot)

                val allSlots = marketApi.getSlots(isExpired = false)
                assertNotNull(allSlots)
                assertTrue(allSlots.none { it.id == insertedId })
            }
        }
    }

    @Test
    fun GIVEN_multipleSlotsForPlayer_WHEN_countPlayerSlots_THEN_returnsExactCount() {
        runTest {
            withTestContext {
                val uuid = "550e8400-e29b-41d4-a716-446655440001"
                repeat(3) { marketApi.insertSlot(createMarketSlot(uuid = uuid)) }

                val count = marketApi.countPlayerSlots(uuid)

                assertEquals(3, count)
            }
        }
    }

    @Test
    fun GIVEN_noSlotsForPlayer_WHEN_countPlayerSlots_THEN_returnsZero() {
        runTest {
            withTestContext {
                val uuid = "550e8400-e29b-41d4-a716-446655440001"

                val count = marketApi.countPlayerSlots(uuid)

                assertEquals(0, count)
            }
        }
    }

    @Test
    fun GIVEN_slotsForTwoPlayers_WHEN_getUserSlots_THEN_returnsOnlyRequestedPlayerSlots() {
        runTest {
            withTestContext {
                val firstPlayerUuid = "550e8400-e29b-41d4-a716-446655440001"
                val secondPlayerUuid = "550e8400-e29b-41d4-a716-446655440002"
                repeat(2) { marketApi.insertSlot(createMarketSlot(uuid = firstPlayerUuid)) }
                marketApi.insertSlot(createMarketSlot(uuid = secondPlayerUuid))

                val firstPlayerSlots = marketApi.getUserSlots(firstPlayerUuid, isExpired = false)
                assertNotNull(firstPlayerSlots)

                assertEquals(2, firstPlayerSlots.size)
                assertTrue(firstPlayerSlots.all { it.minecraftUuid == firstPlayerUuid })
            }
        }
    }

    @Test
    fun GIVEN_activeAndExpiredSlotsForPlayer_WHEN_getUserActiveSlots_THEN_returnsOnlyActiveSlots() {
        runTest {
            withTestContext {
                val uuid = "550e8400-e29b-41d4-a716-446655440001"
                marketApi.insertSlot(createMarketSlot(uuid = uuid, expired = false))
                marketApi.insertSlot(createMarketSlot(uuid = uuid, expired = true))

                val activeUserSlots = marketApi.getUserSlots(uuid, isExpired = false)
                assertNotNull(activeUserSlots)

                assertEquals(1, activeUserSlots.size)
                assertTrue(activeUserSlots.all { !it.expired })
            }
        }
    }

    @Test
    fun GIVEN_slotCreatedLongAgo_WHEN_getSlotsOlderThan_THEN_slotIsIncluded() {
        runTest {
            withTestContext {
                val oneHourInMillis = 60 * 60 * 1000L
                marketApi.insertSlot(createMarketSlot(timeOffset = -oneHourInMillis * 2))

                val oldSlots = marketApi.getSlotsOlderThan(oneHourInMillis)
                assertNotNull(oldSlots)

                assertEquals(1, oldSlots.size)
            }
        }
    }

    @Test
    fun GIVEN_recentlyCreatedSlot_WHEN_getSlotsOlderThan_THEN_slotIsNotIncluded() {
        runTest {
            withTestContext {
                val oneHourInMillis = 60 * 60 * 1000L
                marketApi.insertSlot(createMarketSlot(timeOffset = 0L))

                val oldSlots = marketApi.getSlotsOlderThan(oneHourInMillis)
                assertNotNull(oldSlots)

                assertTrue(oldSlots.isEmpty())
            }
        }
    }

    @Test
    fun GIVEN_mixOfOldAndNewSlots_WHEN_getSlotsOlderThan_THEN_returnsOnlyOldSlots() {
        runTest {
            withTestContext {
                val oneHourInMillis = 60 * 60 * 1000L
                marketApi.insertSlot(createMarketSlot(timeOffset = -oneHourInMillis * 2))
                marketApi.insertSlot(createMarketSlot(timeOffset = -oneHourInMillis * 3))
                marketApi.insertSlot(createMarketSlot(timeOffset = 0L))

                val oldSlots = marketApi.getSlotsOlderThan(oneHourInMillis)
                assertNotNull(oldSlots)

                assertEquals(2, oldSlots.size)
            }
        }
    }
}
