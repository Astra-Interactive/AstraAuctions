package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.api.market.di.ApiMarketModule
import com.astrainteractive.astramarket.gui.di.AuctionGuiModule
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.DefaultDatabase
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    val plugin: Lateinit<AstraMarket>
    val configFileManager: Single<DefaultSpigotFileManager>
    val bukkitSerializer: Single<Serializer>
    val translation: Reloadable<Translation>
    val configuration: Reloadable<AuctionConfig>
    val database: Single<DefaultDatabase>
    val bStats: Single<Metrics>
    val vaultEconomyProvider: Single<EconomyProvider>

    val scope: Single<AsyncComponent>
    val dispatchers: Single<BukkitDispatchers>
    val logger: Single<Logger>

    val apiMarketModule: ApiMarketModule
    val auctionGuiModule: AuctionGuiModule
}
