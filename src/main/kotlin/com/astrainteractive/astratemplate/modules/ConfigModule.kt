package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.utils.AuctionConfig
import com.astrainteractive.astratemplate.utils.Files
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.utils.toClass

object ConfigModule : IReloadable<AuctionConfig>() {
    override fun initializer(): AuctionConfig {
        return EmpireSerializer.toClass<AuctionConfig>(Files.configFile) ?: AuctionConfig()
    }
}