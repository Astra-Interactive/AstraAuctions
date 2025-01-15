package ru.astrainteractive.astramarket

import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.core.LifecyclePlugin
import ru.astrainteractive.astramarket.di.RootModule

class AstraMarket : LifecyclePlugin(), Logger by JUtiltLogger("AstraMarket") {
    private val rootModule = RootModule.Default(this)

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
