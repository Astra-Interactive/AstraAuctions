package com.astrainteractive.astratemplate.api

import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astratemplate.AstraMarket
import com.astrainteractive.astratemplate.sqldatabase.Database
import kotlinx.coroutines.launch
import java.util.*

object AuctionExpireChecker {
    var job: Timer? = null

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        Logger.log("Expired auction checker job has started", Repository.TAG, consolePrint = false)
        job = kotlin.concurrent.timer("auction_checker", daemon = true, 0L, 2000L) {
            if (!Database.isInitialized) return@timer
            AsyncHelper.launch {
                val auctions = Repository.fetchOldAuctions(AstraMarket.pluginConfig.auction.maxTime * 1000)
                auctions?.forEach {
                    val res = Repository.expireAuction(it)
                    Logger.log(
                        "Found expired auction ${it}. Expiring result: $res",
                        Repository.TAG,
                        consolePrint = false
                    )
                }
            }
        }

    }


    fun stopAuctionChecker() {
        Logger.log("Expired auction checker job has stopped", Repository.TAG, consolePrint = false)
        job?.cancel()
    }
}