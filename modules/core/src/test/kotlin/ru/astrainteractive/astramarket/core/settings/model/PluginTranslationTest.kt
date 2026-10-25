@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.core.settings.model

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PluginTranslationTest {

    private val translation = PluginTranslation()

    private fun clickEventsOf(message: LocalizableComponent): List<ClickEvent> {
        fun collectClickEvents(component: Component): List<ClickEvent> {
            return listOfNotNull(component.clickEvent()) + component.children().flatMap(::collectClickEvents)
        }
        return collectClickEvents(message.toComponent(MinecraftLocales.RU_RU))
    }

    private fun plainText(message: LocalizableComponent): String {
        return PlainTextComponentSerializer.plainText().serialize(message.toComponent(MinecraftLocales.RU_RU))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_buyer_is_notified_THEN_message_has_no_click() {
        val message = translation.buy.bought(
            sellerName = "seller",
            itemName = CLICK_ITEM_NAME,
            price = 10
        )

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_owner_is_notified_THEN_message_has_no_click() {
        val message = translation.buy.sold(
            buyerName = "buyer",
            itemName = CLICK_ITEM_NAME,
            price = 10
        )

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_click_tag_WHEN_auction_expires_THEN_message_has_no_click() {
        val message = translation.expire.ownerNotice(
            itemName = CLICK_ITEM_NAME,
            price = 10
        )

        assertEquals(emptyList(), clickEventsOf(message))
    }

    @Test
    fun GIVEN_item_name_with_color_tag_WHEN_buyer_is_notified_THEN_tag_is_shown_as_text() {
        val message = translation.buy.bought(
            sellerName = "seller",
            itemName = "<red>Алмаз",
            price = 10
        )

        assertTrue("<red>Алмаз" in plainText(message), plainText(message))
    }

    companion object {
        private const val CLICK_ITEM_NAME = "<click:run_command:'/pay seller 1000'>Алмаз</click>"
    }
}
