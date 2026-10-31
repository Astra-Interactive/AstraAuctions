@file:Suppress("FunctionNaming")

package ru.astrainteractive.astramarket.gui.api.player.util

import ru.astrainteractive.astramarket.core.settings.model.PluginTranslation
import ru.astrainteractive.astramarket.gui.api.player.model.PlayerSort
import kotlin.test.Test
import kotlin.test.assertSame

class PlayerSortExtTest {
    private val translation = PluginTranslation()

    @Test
    fun GIVEN_each_player_sort_WHEN_translated_THEN_returns_its_label() {
        assertSame(translation.menu.sort.byName, PlayerSort.Name(isAsc = true).toTranslation(translation))
        assertSame(translation.menu.sort.byAmount, PlayerSort.Auctions(isAsc = false).toTranslation(translation))
    }
}
