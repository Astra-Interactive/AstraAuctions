package ru.astrainteractive.astramarket.service.executor

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.astrainteractive.astralibs.service.ServiceExecutor
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import kotlin.collections.filter
import kotlin.collections.map
import kotlin.collections.orEmpty
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class ExpireServiceExecutor(
    private val marketApi: MarketApi,
    private val configKrate: CachedKrate<PluginConfig>
) : ServiceExecutor {
    override suspend fun doWork() {
        val config by configKrate
        coroutineScope {
            val maxAuctionLifeTime = config.auction.maxTimeSeconds.seconds
            val currentTime = System.currentTimeMillis().milliseconds
            marketApi.getSlots(isExpired = false)
                .orEmpty()
                .filter { currentTime - it.time.milliseconds > maxAuctionLifeTime }
                .map { async { marketApi.expireSlot(it) } }
                .awaitAll()
        }
    }
}
