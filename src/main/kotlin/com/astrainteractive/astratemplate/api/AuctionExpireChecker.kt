package com.astrainteractive.astratemplate.api

import com.astrainteractive.astratemplate.AstraMarket
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.database.isConnected
import java.util.*

object AuctionExpireChecker {
    var job: Timer? = null

    /**
     * Start job for auction expire checking
     */
    fun startAuctionChecker() {
        Logger.log("Expired auction checker job has started", Repository.TAG, consolePrint = false)
        job = kotlin.concurrent.timer("auction_checker", daemon = true, 0L, 2000L) {
            if (!Database.instance?.connection.isConnected) return@timer
            PluginScope.launch {
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