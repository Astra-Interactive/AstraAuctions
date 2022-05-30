package com.astrainteractive.astratemplate.events

import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astralibs.menu.MenuListener


/**
 * Handler for all your events
 */
class EventHandler() : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()

    init {
        MenuListener().onEnable(this)
    }

    override fun onDisable() {
        super.onDisable()
    }
}
