package ru.astrainteractive.astramarket.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.astramarket.api.market.MarketApi
import ru.astrainteractive.astramarket.core.PluginConfig
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class ExpireServiceTask(
    private val marketApi: MarketApi,
    private val configKrate: CachedKrate<PluginConfig>
) : ServiceTask {
    override suspend fun execute() {
        val config by configKrate
        coroutineScope {
            val maxAuctionLifeTime = config.auction.maxTimeSeconds.seconds
            val currentTime = System.currentTimeMillis().milliseconds
            marketApi.getSlots(isExpired = false)
                .orEmpty()
                .filter { slot -> currentTime - slot.time.milliseconds > maxAuctionLifeTime }
                .map { slot -> async { marketApi.expireSlot(slot) } }
                .awaitAll()
        }
    }
}
