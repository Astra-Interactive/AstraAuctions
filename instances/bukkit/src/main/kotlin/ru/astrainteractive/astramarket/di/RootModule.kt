package ru.astrainteractive.astramarket.di

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astralibs.encoding.model.EncodedObject
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astramarket.command.di.CommandModule
import ru.astrainteractive.astramarket.core.di.BukkitCoreModule
import ru.astrainteractive.astramarket.core.itemstack.ItemStackSerializer
import ru.astrainteractive.astramarket.db.market.entity.AuctionTableV1
import ru.astrainteractive.astramarket.db.market.entity.AuctionTableV2
import ru.astrainteractive.astramarket.gui.router.di.RouterModule
import ru.astrainteractive.astramarket.market.data.di.BukkitMarketDataModule
import ru.astrainteractive.astramarket.market.di.MarketViewModule
import ru.astrainteractive.astramarket.market.domain.di.BukkitMarketDomainModule
import ru.astrainteractive.astramarket.players.di.PlayersMarketViewModule
import ru.astrainteractive.astramarket.worker.di.WorkerModule

internal class RootModule(plugin: LifecyclePlugin) : Logger by JUtiltLogger("AstraMarket-RootModule") {
    val coreModule: BukkitCoreModule = BukkitCoreModule.Default(plugin)

    val apiMarketModule: ApiMarketModule = ApiMarketModule.Default(
        dispatchers = coreModule.dispatchers,
        yamlStringFormat = coreModule.yamlStringFormat,
        dataFolder = coreModule.plugin.dataFolder,
        scope = coreModule.scope,
        onMigration = onMigration@{ database ->
            transaction(database) {
                if (AuctionTableV2.exists()) return@transaction
                info { "Starting migration...." }
                SchemaUtils.create(AuctionTableV2)
                AuctionTableV1
                    .selectAll()
                    .map { resultRow ->
                        val byteArrayItem = EncodedObject.ByteArray(resultRow[AuctionTableV1.item])
                        val itemStack = coreModule.itemStackEncoder.toItemStack(byteArrayItem)
                        val itemStackString = ItemStackSerializer.encodeToString(itemStack)
                        AuctionTableV2.insert {
                            it[AuctionTableV2.minecraftUuid] = resultRow[AuctionTableV1.minecraftUuid]
                            it[AuctionTableV2.minecraftUsername] = resultRow[AuctionTableV1.minecraftUsername]
                            it[AuctionTableV2.time] = resultRow[AuctionTableV1.time]
                            it[AuctionTableV2.item] = itemStackString
                            it[AuctionTableV2.price] = resultRow[AuctionTableV1.price]
                            it[AuctionTableV2.expired] = resultRow[AuctionTableV1.expired]
                        }
                    }
                SchemaUtils.drop(AuctionTableV1)
            }
        }
    )

    val marketViewModule: MarketViewModule = MarketViewModule.Default(
        coreModule = coreModule,
        apiMarketModule = apiMarketModule,
        marketDataModule = BukkitMarketDataModule(
            itemStackEncoder = coreModule.itemStackEncoder,
            stringSerializer = coreModule.kyoriComponentSerializer.cachedValue
        ),
        platformMarketDomainModule = BukkitMarketDomainModule(
            itemStackEncoder = coreModule.itemStackEncoder,
        )
    )

    val playersMarketViewModule: PlayersMarketViewModule = PlayersMarketViewModule.Default(
        coreModule = coreModule,
        apiMarketModule = apiMarketModule
    )

    val routerModule: RouterModule = RouterModule.Default(
        coreModule = coreModule,
        marketViewModule = marketViewModule,
        bukkitCoreModule = coreModule,
        playersMarketViewModule = playersMarketViewModule
    )

    val commandModule: CommandModule = CommandModule.Default(
        coreModule = coreModule,
        bukkitCoreModule = coreModule,
        routerModule = routerModule,
        marketViewModule = marketViewModule
    )

    val workerModule: WorkerModule = WorkerModule.Default(
        apiMarketModule = apiMarketModule,
        coreModule = coreModule
    )

    private val lifecycles: List<Lifecycle>
        get() = listOf(
            coreModule.lifecycle,
            apiMarketModule.lifecycle,
            commandModule.lifecycle,
            workerModule.lifecycle
        )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            lifecycles.forEach(Lifecycle::onEnable)
        },
        onReload = {
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
            lifecycles.forEach(Lifecycle::onReload)
        },
        onDisable = {
            Bukkit.getOnlinePlayers().forEach(Player::closeInventory)
            HandlerList.unregisterAll(coreModule.plugin)
            lifecycles.forEach(Lifecycle::onDisable)
        }

    )
}
