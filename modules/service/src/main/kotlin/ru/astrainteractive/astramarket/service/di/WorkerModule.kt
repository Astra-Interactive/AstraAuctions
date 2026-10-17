package ru.astrainteractive.astramarket.service.di

import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.astramarket.core.di.CoreModule
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.astramarket.service.ExpireServiceTask
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

interface WorkerModule {
    val lifecycle: Lifecycle

    class Default(
        marketRepository: MarketRepository,
        coreModule: CoreModule
    ) : WorkerModule {
        private val expireService = IntervalService(
            interval = EXPIRE_CHECK_INTERVAL,
            scope = coreModule.ioScope,
            logger = JUtiltLogger("ExpireService"),
            task = ExpireServiceTask(
                marketRepository = marketRepository,
                configKrate = coreModule.configKrate,
                clock = Clock.System
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                expireService.onEnable()
            },
            onDisable = {
                expireService.onDisable()
            }
        )

        companion object {
            private val EXPIRE_CHECK_INTERVAL = 1.minutes
        }
    }
}
