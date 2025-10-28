package ru.astrainteractive.astramarket.service.di

import kotlinx.coroutines.flow.flowOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.Service
import ru.astrainteractive.astralibs.service.TickFlowService
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.service.executor.ExpireServiceExecutor
import kotlin.time.Duration.Companion.minutes

interface WorkerModule {
    val lifecycle: Lifecycle

    class Default(
        apiMarketModule: ApiMarketModule,
        coreModule: CoreModule
    ) : WorkerModule {
        private val expireServiceExecutor: Service by lazy {
            TickFlowService(
                coroutineContext = coreModule.dispatchers.IO,
                delay = flowOf(1.minutes),
                executor = ExpireServiceExecutor(
                    marketApi = apiMarketModule.marketApi,
                    configKrate = coreModule.configKrate
                )
            )
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                expireServiceExecutor.onCreate()
            },
            onDisable = {
                expireServiceExecutor.onDestroy()
            }
        )
    }
}
