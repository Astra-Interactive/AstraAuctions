package ru.astrainteractive.astramarket.router.api

import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import java.util.UUID

interface GuiRouter {
    sealed interface Route {
        val inventoryOwner: OnlineKPlayer

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
