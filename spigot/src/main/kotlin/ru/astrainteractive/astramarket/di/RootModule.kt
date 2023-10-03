package ru.astrainteractive.astramarket.di

import org.bstats.bukkit.Metrics
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.encoding.Encoder
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.permission.PermissionManager
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astramarket.AstraMarket
import ru.astrainteractive.astramarket.domain.di.BukkitDomainModule
import ru.astrainteractive.astramarket.domain.di.SharedDomainModule
import ru.astrainteractive.astramarket.plugin.AuctionConfig
import ru.astrainteractive.astramarket.plugin.Translation
import ru.astrainteractive.astramarket.presentation.di.AuctionGuiModule
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single

interface RootModule : Module {
    val plugin: Lateinit<AstraMarket>
    val encoder: Single<Encoder>
    val translation: Reloadable<Translation>
    val configuration: Reloadable<AuctionConfig>
    val database: Single<Database>
    val bStats: Single<Metrics>
    val economyProvider: Single<EconomyProvider>
    val permissionManager: Single<PermissionManager>

    val scope: Single<AsyncComponent>
    val inventoryClickEventListener: Single<EventListener>
    val dispatchers: Single<BukkitDispatchers>
    val logger: Single<Logger>
    val stringSerializer: Single<KyoriComponentSerializer>

    val dataModule: DataModule
    val auctionGuiModule: AuctionGuiModule
    val bukkitDomainModule: BukkitDomainModule
    val sharedDomainModule: SharedDomainModule
}
