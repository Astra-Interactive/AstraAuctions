package ru.astrainteractive.astramarket.worker.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.di.ApiMarketModule
import ru.astrainteractive.astramarket.worker.Worker
import ru.astrainteractive.astramarket.worker.expireworker.ExpireWorker

interface WorkerModule {
    val lifecycle: Lifecycle

    class Default(
        apiMarketModule: ApiMarketModule,
        coreModule: CoreModule
    ) : WorkerModule {
        private val expireWorker: Worker by lazy {
            ExpireWorker(
                marketApi = apiMarketModule.marketApi,
                dispatchers = coreModule.dispatchers,
                configKrate = coreModule.config
            )
        }
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                expireWorker.start()
            },
            onDisable = {
                expireWorker.stop()
            }
        )
    }
}
