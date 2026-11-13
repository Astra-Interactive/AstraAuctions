@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.gui.bukkit.player.util

import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort
import kotlin.test.Test
import kotlin.test.assertSame

class PlayerSortTranslationTest {
    private val translation = PluginTranslation()

    @Test
    fun GIVEN_each_player_sort_WHEN_translated_THEN_returns_its_label() {
        assertSame(
            translation.menu.sort.byName,
            selectPlayerSortTranslation(PlayerSort.Name(isAsc = true), translation)
        )
        assertSame(
            translation.menu.sort.byAmount,
            selectPlayerSortTranslation(PlayerSort.Auctions(isAsc = false), translation)
        )
    }
}
