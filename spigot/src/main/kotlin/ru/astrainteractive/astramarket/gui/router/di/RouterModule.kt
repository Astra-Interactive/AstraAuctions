package ru.astrainteractive.astramarket.gui.router.di

import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.GuiRouterImpl

interface RouterModule {
    val router: GuiRouter

    @Suppress("LongParameterList")
    class Default(rootModule: RootModule) : RouterModule {
        override val router: GuiRouter = GuiRouterImpl(rootModule)
    }
}
