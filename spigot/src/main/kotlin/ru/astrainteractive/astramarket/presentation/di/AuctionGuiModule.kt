package ru.astrainteractive.astramarket.presentation.di

import ru.astrainteractive.astramarket.di.RootModule
import ru.astrainteractive.astramarket.presentation.router.GuiRouter
import ru.astrainteractive.astramarket.presentation.router.GuiRouterImpl
import ru.astrainteractive.klibs.kdi.Module

interface AuctionGuiModule : Module {
    val router: GuiRouter

    @Suppress("LongParameterList")
    class Default(rootModule: RootModule) : AuctionGuiModule {

        override val router: GuiRouter = GuiRouterImpl(rootModule)
    }
}
