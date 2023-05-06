package com.astrainteractive.astramarket.di

import com.astrainteractive.astramarket.AstraMarket
import com.astrainteractive.astramarket.domain.api.AuctionsAPI
import com.astrainteractive.astramarket.plugin.AuctionConfig
import com.astrainteractive.astramarket.plugin.Translation
import org.bstats.bukkit.Metrics
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.astralibs.Lateinit
import ru.astrainteractive.astralibs.Module
import ru.astrainteractive.astralibs.Reloadable
import ru.astrainteractive.astralibs.Single
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.VaultEconomyProvider
import ru.astrainteractive.astralibs.encoding.Serializer
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.DefaultDatabase

interface RootModule : Module {
    val plugin: Lateinit<AstraMarket>
    val configFileManager: Single<DefaultSpigotFileManager>
    val bukkitSerializer: Single<Serializer>
    val translation: Reloadable<Translation>
    val configuration: Reloadable<AuctionConfig>
    val database: Single<DefaultDatabase>
    val auctionsApi: Single<AuctionsAPI>
    val bStats: Single<Metrics>
    val vaultEconomyProvider: Single<VaultEconomyProvider>

    @OptIn(UnsafeApi::class)
    val scope: Single<AsyncComponent>

    @OptIn(UnsafeApi::class)
    val dispatchers: Single<BukkitDispatchers>
    val logger: Single<Logger>
}
