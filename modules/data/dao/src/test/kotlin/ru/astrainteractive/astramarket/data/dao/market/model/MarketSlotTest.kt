@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.data.dao.market.model

import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class MarketSlotTest {
    private val listedAt = Instant.fromEpochMilliseconds(1_000_000)
    private val slot = MarketSlot(
        id = 1,
        minecraftUuid = "550e8400-e29b-41d4-a716-446655440001",
        minecraftUsername = "Trader",
        time = listedAt,
        item = EncodedObject.ByteArray(byteArrayOf()),
        price = 10f,
        expired = false
    )

    @Test
    fun GIVEN_slot_listed_longer_than_lifetime_WHEN_checked_THEN_lifetime_is_over() {
        assertTrue(slot.isLifetimeOver(now = listedAt + 2.hours, maxLifetime = 1.hours))
    }

    @Test
    fun GIVEN_slot_listed_shorter_than_lifetime_WHEN_checked_THEN_lifetime_is_not_over() {
        assertFalse(slot.isLifetimeOver(now = listedAt + 30.minutes, maxLifetime = 1.hours))
    }

    @Test
    fun GIVEN_slot_listed_exactly_lifetime_ago_WHEN_checked_THEN_lifetime_is_not_over() {
        assertFalse(slot.isLifetimeOver(now = listedAt + 1.hours, maxLifetime = 1.hours))
    }
}
