package ru.astrainteractive.astramarket.data.exposed.di

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
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.data.exposed.market.ExposedMarketRepository
import ru.astrainteractive.astramarket.data.exposed.model.DatabaseConfig
import ru.astrainteractive.astramarket.data.exposed.table.AuctionTable
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.mikro.exposed.util.connectAsFlow
import java.io.File
import java.sql.Connection

class ExposedModule(
    dispatchers: KotlinDispatchers,
    yamlStringFormat: StringFormat,
    dataFolder: File,
    ioScope: CoroutineScope,
    default: () -> DatabaseConfiguration,
) {
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
        .map(::databaseConfiguration)
        .distinctUntilChanged()
        .flatMapLatest(::connectDatabase)
        .onEach { database ->
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction(database) {
                SchemaUtils.create(AuctionTable)
            }
        }
        .shareIn(
            scope = ioScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    val marketRepository: MarketRepository = ExposedMarketRepository(
        databaseFlow = databaseFlow,
        dispatchers = dispatchers
    )

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onReload = { dbConfig.getValue() }
        )
    }

    private fun databaseConfiguration(config: DatabaseConfig): DatabaseConfiguration {
        return config.configuration
    }

    private fun connectDatabase(configuration: DatabaseConfiguration): Flow<Database> {
        return configuration.connectAsFlow()
    }
}
