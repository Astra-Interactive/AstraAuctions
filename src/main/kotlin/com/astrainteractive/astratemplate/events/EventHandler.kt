package com.astrainteractive.astratemplate.events

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager
import com.astrainteractive.astralibs.menu.MenuListener


/**
 * Handler for all your events
 */
class EventHandler() : IAstraManager {
    override val handlers: MutableList<IAstraListener> = mutableListOf()
    init {
        MenuListener().onEnable(this)
    }
}