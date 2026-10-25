@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ListExtTest {
    private val sorts = listOf(
        "first",
        "second",
        "third"
    )

    @Test
    fun GIVEN_current_sort_WHEN_next_is_taken_forwards_THEN_returns_the_following_sort() {
        assertEquals("second", sorts.nextAfter("first", backwards = false))
    }

    @Test
    fun GIVEN_last_sort_WHEN_next_is_taken_forwards_THEN_wraps_to_the_first_sort() {
        assertEquals("first", sorts.nextAfter("third", backwards = false))
    }

    @Test
    fun GIVEN_first_sort_WHEN_next_is_taken_backwards_THEN_wraps_to_the_last_sort() {
        assertEquals("third", sorts.nextAfter("first", backwards = true))
    }

    @Test
    fun GIVEN_unknown_sort_WHEN_next_is_taken_THEN_returns_the_first_sort() {
        assertEquals("first", sorts.nextAfter("unknown", backwards = true))
    }
}
