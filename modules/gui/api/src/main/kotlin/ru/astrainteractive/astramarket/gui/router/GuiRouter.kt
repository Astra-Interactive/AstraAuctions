package ru.astrainteractive.astramarket.gui.router

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        val inventoryOwner: OnlineKPlayer

        /**
         * @param inventoryOwner player who opened route
         * @param isExpired expired auctions
         * @param targetPlayerUUID only this player auctions will be shown
         */
        class Slots(
            override val inventoryOwner: OnlineKPlayer,
            val isExpired: Boolean,
            val targetPlayerUUID: UUID?
        ) : Route

        class Players(
            override val inventoryOwner: OnlineKPlayer,
            val isExpired: Boolean
        ) : Route
    }

    fun navigate(route: Route)
}
