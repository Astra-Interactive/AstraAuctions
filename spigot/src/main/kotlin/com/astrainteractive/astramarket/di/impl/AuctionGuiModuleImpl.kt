package com.astrainteractive.astramarket.di.impl

import com.astrainteractive.astramarket.di.GuiModule
import com.astrainteractive.astramarket.di.RootModule
import com.astrainteractive.astramarket.di.UseCasesModule
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.gui.di.AuctionGuiModule
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class AuctionGuiModuleImpl(rootModule: RootModule) : AuctionGuiModule {
    override val config: AuctionConfig by rootModule.configuration
    override val translation: Translation by rootModule.translation
    override val serializer: Serializer by rootModule.bukkitSerializer
    override val scope: AsyncComponent by rootModule.scope
    override val dispatchers by rootModule.dispatchers
    override val dataSource: AuctionsAPI by rootModule.auctionsApi

    override val guiModule: GuiModule by Provider {
        rootModule.guiModule
    }
    override val useCasesModule: UseCasesModule by Provider {
        rootModule.useCasesModule
    }
}
