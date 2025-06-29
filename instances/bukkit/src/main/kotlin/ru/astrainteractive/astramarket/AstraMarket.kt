package ru.astrainteractive.astramarket

import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astramarket.di.RootModule

class AstraMarket : LifecyclePlugin() {
    private val rootModule = RootModule(this)

    override fun onEnable() {
        rootModule.lifecycle.onEnable()
    }

    override fun onDisable() {
        rootModule.lifecycle.onDisable()
    }

    override fun onReload() {
        rootModule.lifecycle.onReload()
    }
}
