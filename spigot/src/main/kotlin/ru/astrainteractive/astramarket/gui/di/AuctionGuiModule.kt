package ru.astrainteractive.astramarket.gui.di

import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.gui.router.GuiRouter
import ru.astrainteractive.astramarket.gui.router.GuiRouterImpl

interface AuctionGuiModule {
    val router: GuiRouter

    @Suppress("LongParameterList")
    class Default(rootModule: RootModule) : AuctionGuiModule {

        override val router: GuiRouter = GuiRouterImpl(rootModule)
    }
}
