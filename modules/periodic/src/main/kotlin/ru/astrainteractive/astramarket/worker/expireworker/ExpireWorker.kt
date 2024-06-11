package ru.astrainteractive.astramarket.worker.expireworker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.astramarket.worker.Worker
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class ExpireWorker(
    private val marketApi: MarketApi,
    dispatchers: KotlinDispatchers,
    val config: Dependency<PluginConfig>
) : Worker("EXPIRE_WORKER") {
    override val dispatcher: CoroutineDispatcher = dispatchers.IO.limitedParallelism(1)
    override val initialDelay: Duration = 5.seconds
    override val period: Duration = 1.minutes

    override suspend fun doWork(): Unit = coroutineScope {
        val maxAuctionLifeTime = config.value.auction.maxTimeSeconds.seconds
        val currentTime = System.currentTimeMillis().milliseconds
        marketApi.getSlots(isExpired = false)
            .orEmpty()
            .filter { currentTime - it.time.milliseconds > maxAuctionLifeTime }
            .map {
                async { marketApi.expireSlot(it) }
            }.awaitAll()
    }
}
