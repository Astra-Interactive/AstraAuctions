package com.astrainteractive.astratemplate.modules

import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.utils.AuctionConfig
import com.astrainteractive.astratemplate.utils.Files
import ru.astrainteractive.astralibs.AstraYamlParser
import ru.astrainteractive.astralibs.di.IReloadable

object ConfigModule : IReloadable<AuctionConfig>() {
    override fun initializer(): AuctionConfig {
        val c = Files.configFile.fileConfiguration
        val config =
            AstraYamlParser.fileConfigurationToClass<AuctionConfig>(c)
                ?: AuctionConfig()
        return config
    }
}