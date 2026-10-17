package ru.astrainteractive.astramarket.service.auction.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.astrainteractive.astralibs.service.ServiceTask
import ru.astrainteractive.astramarket.core.settings.model.PluginConfig
import ru.astrainteractive.astramarket.data.dao.market.api.MarketRepository
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal class ExpireServiceTask(
    private val marketRepository: MarketRepository,
    private val configKrate: CachedKrate<PluginConfig>,
    private val clock: Clock
) : ServiceTask {
    override suspend fun execute() {
        val config by configKrate
        coroutineScope {
            val maxAuctionLifeTime = config.auction.maxTimeSeconds.seconds
            val now = clock.now()
            marketRepository.getSlots(isExpired = false)
                .getOrDefault(emptyList())
                .filter { slot -> slot.isLifetimeOver(now, maxAuctionLifeTime) }
                .map { slot -> async { marketRepository.expireSlot(slot) } }
                .awaitAll()
        }
    }
}
