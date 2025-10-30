package ru.astrainteractive.astramarket.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.impl.ExposedMarketApi
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.model.DatabaseConfig
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.coroutines.mapCached
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connect
import java.io.File

interface ApiMarketModule {
    val lifecycle: Lifecycle

    val marketApi: MarketApi

    class Default(
        dispatchers: KotlinDispatchers,
        yamlStringFormat: StringFormat,
        dataFolder: File,
        ioScope: CoroutineScope,
        default: () -> DatabaseConfiguration,
    ) : ApiMarketModule {
        private val dbConfig = DefaultMutableKrate(
            factory = { DatabaseConfig(default.invoke()) },
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = dataFolder.resolve("database.yaml"),
                    default = { DatabaseConfig(default.invoke()) }
                )
            }
        ).asStateFlowMutableKrate()

        private val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow
            .map { it.configuration }
            .distinctUntilChanged()
            .mapCached(
                scope = ioScope,
                dispatcher = dispatchers.IO,
                transform = { dbConfig, previous ->
                    previous?.run(TransactionManager::closeAndUnregister)
                    val database = dbConfig.connect()
                    TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                    transaction(database) {
                        addLogger(Slf4jSqlDebugLogger)
                        SchemaUtils.create(AuctionTable)
                        SchemaUtils.createMissingTablesAndColumns(AuctionTable)
                    }
                    database
                }
            )

        override val marketApi: MarketApi = ExposedMarketApi(
            databaseFlow = databaseFlow,
            dispatchers = dispatchers
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                },
                onDisable = {
                    GlobalScope.launch(NonCancellable) {
                        databaseFlow.first().run(TransactionManager::closeAndUnregister)
                    }
                    ioScope.cancel()
                },
                onReload = {
                    dbConfig.getValue()
                }
            )
        }
    }
}
