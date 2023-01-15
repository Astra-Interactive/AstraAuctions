package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.modules.ConfigModule
import com.astrainteractive.astratemplate.modules.DataSourceModule
import com.astrainteractive.astratemplate.modules.DatabaseModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.di.getValue
import java.util.*

object AuctionExpireChecker {
    const val TAG = "AuctionExpireChecker"
    var job: Timer? = null
    private val database by DatabaseModule
    private val dataSource by DataSourceModule
    private val config by ConfigModule

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        return
        Logger.log("Expired auction checker job has started", TAG, consolePrint = false)
        job = kotlin.concurrent.timer("auction_checker", daemon = true, 0L, 2000L) {
            if (!database.isConnected) return@timer
            PluginScope.launch(Dispatchers.IO) {
                val auctions = dataSource.getAuctionsOlderThan(config.auction.maxTime * 1000)
                auctions?.forEach {
                    val res = dataSource.expireAuction(it)
                    Logger.log(
                        "Found expired auction ${it}. Expiring result: $res",
                        TAG,
                        consolePrint = false
                    )
                }
            }
        }

    }


    fun stopAuctionChecker() {
        Logger.log("Expired auction checker job has stopped", TAG, consolePrint = false)
        job?.cancel()
    }
}