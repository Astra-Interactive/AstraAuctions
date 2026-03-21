package ru.astrainteractive.astramarket.gui.router

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        /**
         * @param player player who opened route
         * @param isExpired expired auctions
         * @param targetPlayerUUID only this player auctions will be shown
         */
        class Slots(
            val player: OnlineKPlayer,
            val isExpired: Boolean,
            val targetPlayerUUID: UUID?
        ) : Route

        class Players(
            val player: OnlineKPlayer,
            val isExpired: Boolean
        ) : Route
    }

    fun navigate(route: Route)
}
