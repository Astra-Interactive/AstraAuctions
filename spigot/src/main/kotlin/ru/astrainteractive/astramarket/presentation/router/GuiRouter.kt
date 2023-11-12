package ru.astrainteractive.astramarket.presentation.router

import org.bukkit.entity.Player

interface GuiRouter {
    sealed interface Route {
        class Auctions(val player: Player) : Route
        class ExpiredAuctions(val player: Player) : Route
    }

    fun navigate(route: Route)
}
