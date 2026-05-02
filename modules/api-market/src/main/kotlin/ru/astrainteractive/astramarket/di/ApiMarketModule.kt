package ru.astrainteractive.astramarket.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.StringFormat
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.util.parseOrWriteIntoDefault
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.api.market.impl.ExposedMarketApi
import ru.astrainteractive.astramarket.db.market.entity.AuctionTable
import ru.astrainteractive.astramarket.model.DatabaseConfig
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
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
            .map { databaseConfig -> databaseConfig.configuration }
            .distinctUntilChanged()
            .flatMapLatest { configuration -> configuration.connectAsFlow() }
            .onEach { database ->
                TransactionManager.manager.defaultIsolationLevel = java.sql.Connection.TRANSACTION_SERIALIZABLE
                transaction(database) {
                    SchemaUtils.create(AuctionTable)
                }
            }
            .shareIn(ioScope, SharingStarted.Eagerly, 1)

        override val marketApi: MarketApi = ExposedMarketApi(
            databaseFlow = databaseFlow,
            dispatchers = dispatchers
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onReload = {
                    dbConfig.getValue()
                }
            )
        }
    }
}
