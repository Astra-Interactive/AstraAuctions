package com.astrainteractive.astramarket.gui.di

import com.astrainteractive.astramarket.di.GuiModule
import com.astrainteractive.astramarket.di.UseCasesModule
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.klibs.kdi.Module

interface AuctionGuiModule : Module {
    val config: AuctionConfig
    val translation: Translation
    val serializer: Serializer
    val scope: AsyncComponent
    val dispatchers: BukkitDispatchers
    val dataSource: AuctionsAPI

    val guiModule: GuiModule
    val useCasesModule: UseCasesModule
}
