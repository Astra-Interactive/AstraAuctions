package com.astrainteractive.astramarket.command.di

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.di.GuiModule
import com.astrainteractive.astramarket.di.UseCasesModule
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.klibs.kdi.Module

interface CommandsModule : Module {
    val translation: Translation
    val configuration: AuctionConfig
    val plugin: AstraMarket
    val scope: AsyncComponent
    val dispatchers: BukkitDispatchers
    val guiModule: GuiModule
    val useCasesModule: UseCasesModule
}
