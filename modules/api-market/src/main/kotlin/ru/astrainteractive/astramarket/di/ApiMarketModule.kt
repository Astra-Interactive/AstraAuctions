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
import ru.astrainteractive.astralibs.exposed.model.connect
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrWriteIntoDefault
import ru.astrainteractive.astralibs.util.mapCached
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.impl.ExposedMarketApi
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.model.DatabaseConfig
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.kstorage.util.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface ApiMarketModule {
    val lifecycle: Lifecycle

    val marketApi: MarketApi

    class Default(
        dispatchers: KotlinDispatchers,
        yamlStringFormat: StringFormat,
        dataFolder: File,
        scope: CoroutineScope
    ) : ApiMarketModule {
        private val dbConfig = DefaultMutableKrate(
            factory = ::DatabaseConfig,
            loader = {
                yamlStringFormat.parseOrWriteIntoDefault(
                    file = dataFolder.resolve("database.yaml"),
                    default = ::DatabaseConfig
                )
            }
        ).asStateFlowMutableKrate()

        private val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow
            .map { it.configuration }
            .distinctUntilChanged()
            .mapCached(
                scope = scope,
                dispatcher = dispatchers.IO,
                transform = { dbConfig, previous ->
                    previous?.run(TransactionManager::closeAndUnregister)
                    val database = dbConfig.connect(dataFolder)
                    TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                    transaction(database) {
                        addLogger(Slf4jSqlDebugLogger)
                        SchemaUtils.create(
                            AuctionTable,
                        )
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
                    scope.cancel()
                },
                onReload = {
                    dbConfig.getValue()
                }
            )
        }
    }
}
