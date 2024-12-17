package ru.astrainteractive.astramarket.di

import kotlinx.coroutines.Dispatchers
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
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.exposed.factory.DatabaseFactory
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.FlowExt.mapCached
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.impl.ExposedMarketApi
import ru.astrainteractive.astramarket.core.di.factory.ConfigKrateFactory
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.model.DatabaseConfig
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File

interface ApiMarketModule {
    val lifecycle: Lifecycle

    val marketApi: MarketApi

    class Default(
        dispatchers: KotlinDispatchers,
        yamlStringFormat: StringFormat,
        dataFolder: File,
    ) : ApiMarketModule {
        private val scope = CoroutineFeature.Default(Dispatchers.IO)

        private val dbConfig = ConfigKrateFactory.create(
            fileNameWithoutExtension = "database",
            stringFormat = yamlStringFormat,
            dataFolder = dataFolder,
            factory = ::DatabaseConfig
        )

        private val databaseFlow: Flow<Database> = dbConfig.cachedStateFlow
            .map { it.configuration }
            .distinctUntilChanged()
            .mapCached(scope) { dbConfig, previous ->
                previous?.run(TransactionManager::closeAndUnregister)
                val database = DatabaseFactory(dataFolder).create(dbConfig)
                TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                transaction(database) {
                    addLogger(Slf4jSqlDebugLogger)
                    SchemaUtils.create(
                        AuctionTable,
                    )
                }
                database
            }

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
                    dbConfig.loadAndGet()
                }
            )
        }
    }
}
