package com.astrainteractive.astramarket.utils

import com.astrainteractive.astramarket.modules.Modules
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.getValue
import ru.astrainteractive.astralibs.logging.Logger
import java.util.*

object AuctionExpireChecker {
    const val TAG = "AuctionExpireChecker"
    var job: Timer? = null
    private val database by Modules.database
    private val dataSource by Modules.auctionsApi
    private val config by Modules.configuration
    private val logger: Logger by Modules.logger
    private val scope by Modules.scope
    private val dispatchers by Modules.dispatchers

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        return
        logger.info(TAG, "Expired auction checker job has started")
        job = kotlin.concurrent.timer("auction_checker", daemon = true, 0L, 2000L) {
            if (!database.isConnected) return@timer
            scope.launch(dispatchers.IO) {
                val auctions = dataSource.getAuctionsOlderThan(config.auction.maxTime * 1000)
                auctions?.forEach {
                    val res = dataSource.expireAuction(it)
                    logger.info(
                        "Found expired auction $it. Expiring result: $res",
                        TAG,
                    )
                }
            }
        }
    }

    fun stopAuctionChecker() {
        logger.info(TAG, "Expired auction checker job has stopped")
        job?.cancel()
    }
}
