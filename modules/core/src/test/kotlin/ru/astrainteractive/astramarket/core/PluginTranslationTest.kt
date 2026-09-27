@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import ru.astrainteractive.astralibs.kyori.AutoComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PluginTranslationTest {

    private val auction = PluginTranslation().auction

    /** An anvil-renamed item: without escaping, the buyer would get a clickable command. */
    private val clickItemName = "<click:run_command:'/pay seller 1000'>Алмаз</click>"

    private fun Component.selfAndDescendants(): List<Component> {
        return listOf(this) + children().flatMap { child -> child.selfAndDescendants() }
    }

    private fun clickEventsOf(stringDesc: StringDesc): List<ClickEvent<*>> {
        return AutoComponentSerializer.toComponent(stringDesc)
            .selfAndDescendants()
            .mapNotNull { node -> node.clickEvent() }
    }

    private fun plainText(stringDesc: StringDesc): String {
        return PlainTextComponentSerializer.plainText().serialize(AutoComponentSerializer.toComponent(stringDesc))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_buyer_is_notified_THEN_message_has_no_click() {
        val message = auction.notifyUserBuy(playerOwner = "seller", itemName = clickItemName, price = 10)

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_owner_is_notified_THEN_message_has_no_click() {
        val message = auction.notifyOwnerUserBuy(playerName = "buyer", itemName = clickItemName, price = 10)

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_auction_expires_THEN_message_has_no_click() {
        val message = auction.notifyAuctionExpired(item = clickItemName, price = 10)

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_color_tag_WHEN_buyer_is_notified_THEN_tag_is_shown_as_text() {
        val message = auction.notifyUserBuy(playerOwner = "seller", itemName = "<red>Алмаз", price = 10)

        assertTrue("<red>Алмаз" in plainText(message), plainText(message))
    }
}
